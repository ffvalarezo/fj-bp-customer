import { Injectable, inject } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { AuthService } from "@pichincha/angular-sdk/auth";
import { StorageService } from "@pichincha/angular-sdk/storage";
import { BehaviorSubject, Observable, of } from "rxjs";
import { tap, switchMap, catchError } from "rxjs/operators";
import { environment } from "../../environments/environment";

export interface BackendLoginRequest {
  azureToken: string;
  userInfo: any;
}

export interface BackendTokenResponse {
  jwtToken: string;
  refreshToken?: string;
  expiresIn?: number;
  permissions?: string[];
}

@Injectable({
  providedIn: "root",
})
export class HybridAuthService {
  private readonly API_URL = environment.apiUrl;
  private readonly JWT_TOKEN_KEY = "backend_jwt_token";
  private readonly PERMISSIONS_KEY = "user_permissions";

  private http = inject(HttpClient);
  private router = inject(Router);
  private azureAuthService = inject(AuthService);
  private storageService = inject(StorageService);

  // Subject para manejar el estado de autenticación completa
  private isFullyAuthenticatedSubject = new BehaviorSubject<boolean>(
    this.hasValidBackendToken()
  );
  public isFullyAuthenticated$ =
    this.isFullyAuthenticatedSubject.asObservable();

  constructor() {
    // Suscribirse a cambios de autenticación de Azure
    this.azureAuthService
      .getAccountState()
      .subscribe((isAzureAuthenticated) => {
        if (isAzureAuthenticated) {
          // Si Azure está autenticado, intentar obtener token del backend
          this.authenticateWithBackend();
        } else {
          // Si Azure no está autenticado, limpiar todo
          this.clearBackendAuth();
        }
      });
  }

  /**
   * Inicializar el proceso de autenticación Azure
   */
  initializeAzureAuth(): void {
    this.azureAuthService.initialize(true);
  }

  /**
   * Activar cuenta Azure (redirigir a login)
   */
  activateAzureAccount(): void {
    this.azureAuthService.authenticate();
  }

  /**
   * Verificar si el usuario está completamente autenticado (Azure + Backend)
   */
  isFullyAuthenticated(): boolean {
    return (
      this.azureAuthService.isAuthenticated() && this.hasValidBackendToken()
    );
  }

  /**
   * Obtener token JWT del backend
   */
  getBackendToken(): string | null {
    return this.storageService.get(this.JWT_TOKEN_KEY);
  }

  /**
   * Obtener permisos del usuario
   */
  getUserPermissions(): string[] {
    const permissions = this.storageService.get(this.PERMISSIONS_KEY);
    return permissions ? JSON.parse(permissions) : [];
  }

  /**
   * Autenticarse con el backend usando el token de Azure
   */
  private authenticateWithBackend(): void {
    // Obtener token de Azure
    this.azureAuthService.handleAuthRedirect().subscribe({
      next: (azureTokenResponse) => {
        if (azureTokenResponse) {
          const activeAccount =
            this.azureAuthService.msalService.instance.getActiveAccount();

          if (activeAccount) {
            // Preparar datos para el backend
            const backendLoginRequest: BackendLoginRequest = {
              azureToken: azureTokenResponse.accessToken,
              userInfo: {
                username: activeAccount.username,
                name: activeAccount.name,
                email: activeAccount.username,
                objectId: activeAccount.localAccountId,
              },
            };

            // Llamar al backend para obtener JWT personalizado
            this.exchangeAzureTokenForBackendJWT(backendLoginRequest);
          }
        }
      },
      error: (error) => {
        console.error("Error obteniendo token de Azure:", error);
        this.handleAuthError();
      },
    });
  }

  /**
   * Intercambiar token de Azure por JWT del backend
   */
  private exchangeAzureTokenForBackendJWT(request: BackendLoginRequest): void {
    this.http
      .post<BackendTokenResponse>(
        `${this.API_URL}/auth/azure-exchange`,
        request
      )
      .pipe(
        tap((response) => {
          if (response.jwtToken) {
            // Almacenar token JWT del backend
            this.storageService.set(this.JWT_TOKEN_KEY, response.jwtToken);

            // Almacenar permisos si están disponibles
            if (response.permissions) {
              this.storageService.set(
                this.PERMISSIONS_KEY,
                JSON.stringify(response.permissions)
              );
            }

            // Actualizar estado de autenticación completa
            this.isFullyAuthenticatedSubject.next(true);

            // Redirigir a la página principal
            this.navigateToHome();
          }
        }),
        catchError((error) => {
          console.error("Error intercambiando token con backend:", error);
          this.handleAuthError();
          return of(null);
        })
      )
      .subscribe();
  }

  /**
   * Verificar si tiene un token válido del backend
   */
  private hasValidBackendToken(): boolean {
    const token = this.getBackendToken();
    if (!token) {
      return false;
    }

    try {
      const payload = this.decodeJWT(token);
      const currentTime = Date.now() / 1000;
      return payload.exp > currentTime;
    } catch {
      return false;
    }
  }

  /**
   * Decodificar JWT
   */
  private decodeJWT(token: string): any {
    try {
      const base64Url = token.split(".")[1];
      const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split("")
          .map(function (c) {
            return "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2);
          })
          .join("")
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      throw new Error("Invalid JWT format");
    }
  }

  /**
   * Limpiar autenticación del backend
   */
  private clearBackendAuth(): void {
    this.storageService.remove(this.JWT_TOKEN_KEY);
    this.storageService.remove(this.PERMISSIONS_KEY);
    this.isFullyAuthenticatedSubject.next(false);
  }

  /**
   * Manejar errores de autenticación
   */
  private handleAuthError(): void {
    this.clearBackendAuth();
    this.router.navigate(["/auth/login"]);
  }

  /**
   * Navegar a la página principal después de autenticación exitosa
   */
  private navigateToHome(): void {
    this.router.navigate(["/spf-shell-flujo-bancario-bp-front/cliente"]);
  }

  /**
   * Logout completo (Azure + Backend)
   */
  logout(): void {
    // Limpiar datos del backend
    this.clearBackendAuth();

    // Logout de Azure - verificar método disponible
    if (this.azureAuthService.msalService?.instance) {
      this.azureAuthService.msalService.instance.logoutRedirect();
    }

    // Redirigir a login
    this.router.navigate(["/auth/login"]);
  }

  /**
   * Verificar permisos específicos
   */
  hasPermission(permission: string): boolean {
    const permissions = this.getUserPermissions();
    return permissions.includes(permission);
  }

  /**
   * Verificar múltiples permisos (requiere todos)
   */
  hasAllPermissions(requiredPermissions: string[]): boolean {
    const userPermissions = this.getUserPermissions();
    return requiredPermissions.every((permission) =>
      userPermissions.includes(permission)
    );
  }

  /**
   * Verificar si tiene al menos uno de los permisos
   */
  hasAnyPermission(requiredPermissions: string[]): boolean {
    const userPermissions = this.getUserPermissions();
    return requiredPermissions.some((permission) =>
      userPermissions.includes(permission)
    );
  }
}
