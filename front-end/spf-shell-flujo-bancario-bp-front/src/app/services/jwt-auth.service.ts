import { Injectable, inject } from "@angular/core";
import { HttpService } from "@pichincha/angular-sdk/http";
import { Router } from "@angular/router";
import { BehaviorSubject, Observable, from } from "rxjs";
import { tap, catchError } from "rxjs/operators";

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  refreshToken?: string;
  expiresIn?: number;
  user?: any;
}

export interface UserInfo {
  id: string;
  username: string;
  email?: string;
  roles?: string[];
}

@Injectable({
  providedIn: "root",
})
export class JwtAuthService {
  private readonly TOKEN_KEY = "jwt_token";
  private readonly REFRESH_TOKEN_KEY = "refresh_token";
  private readonly USER_KEY = "user_info";

  private http: HttpService = inject(HttpService);
  private router = inject(Router);

  // Subject para manejar el estado de autenticación
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(
    this.hasValidToken()
  );
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor() {
    // Verificar token al inicializar el servicio
    this.checkTokenValidity();
  }

  private headers = {
    "Content-Type": "application/json",
    "x-guid": "123e4567-e89b-12d3-a456-426614174000",
    "x-channel": "01",
    "x-medium": "01",
    "x-app": "12345",
    "x-session": "session123",
  };

  login(credentials: LoginRequest): Observable<LoginResponse> {
    const loginPromise = this.http.post<LoginResponse>(
      "/auth/login",
      credentials,
      this.headers
    );

    return from(loginPromise).pipe(
      tap((response: any) => {
        if (response?.token) {
          this.setToken(response.token);

          if (response.refreshToken) {
            this.setRefreshToken(response.refreshToken);
          }

          if (response.user) {
            this.setUserInfo(response.user);
          }

          this.isAuthenticatedSubject.next(true);
        } else {
          console.error("Response sin token válido:", response);
          throw new Error("Response inválida del servidor");
        }
      }),
      catchError((error: any) => {
        console.error("Error en login request:", error);
        console.error("Error status:", error.status);
        console.error("Error message:", error.message);
        console.error("Error body:", error.error);

        this.clearAuthData();
        this.isAuthenticatedSubject.next(false);
        throw error;
      })
    );
  }

  /**
   * Realizar logout
   */
  logout(): void {
    this.clearAuthData();
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(["/auth/test-jwt"]);
  }

  /**
   * Obtener token JWT actual
   */
  getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Verificar si el usuario está autenticado
   */
  isAuthenticated(): boolean {
    return this.hasValidToken();
  }

  /**
   * Obtener información del usuario
   */
  getUserInfo(): UserInfo | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }

    try {
      const payload = this.decodeToken(token);
      return {
        id: payload.sub || payload.username,
        username: payload.sub || payload.username,
        email: payload.email,
        roles: payload.roles ? [payload.roles] : [],
      };
    } catch {
      return null;
    }
  }

  /**
   * Obtener roles del usuario desde el token
   */
  getUserRoles(): string[] {
    const token = this.getToken();
    if (!token) {
      return [];
    }

    try {
      const payload = this.decodeToken(token);
      if (payload.roles) {
        // Si roles es un string, convertir a array
        return typeof payload.roles === "string"
          ? [payload.roles]
          : payload.roles;
      }
      return [];
    } catch {
      return [];
    }
  }

  /**
   * Verificar si el usuario tiene un rol específico
   */
  hasRole(role: string): boolean {
    const roles = this.getUserRoles();
    return roles.includes(role);
  }

  /**
   * Obtener el username desde el token
   */
  getUsername(): string | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }

    try {
      const payload = this.decodeToken(token);
      return payload.sub || payload.username || null;
    } catch {
      return null;
    }
  }

  /**
   * Refrescar token JWT
   */
  refreshToken(): Observable<LoginResponse> {
    const refreshToken = sessionStorage.getItem(this.REFRESH_TOKEN_KEY);

    if (!refreshToken) {
      this.logout();
      throw new Error("No refresh token available");
    }

    const refreshPromise = this.http.post<LoginResponse>(
      "/auth/refresh",
      { refreshToken },
      this.headers
    );

    return from(refreshPromise).pipe(
      tap((response: any) => {
        if (response?.token) {
          this.setToken(response.token);
          this.isAuthenticatedSubject.next(true);
        }
      }),
      catchError((error: any) => {
        console.error("Error refreshing token:", error);
        this.logout();
        throw error;
      })
    );
  }

  /**
   * Verificar si el token es válido
   */
  private hasValidToken(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    try {
      const payload = this.decodeToken(token);
      const currentTime = Date.now() / 1000;
      return payload.exp > currentTime;
    } catch {
      return false;
    }
  }

  private decodeToken(token: string): any {
    try {
      const parts = token.split(".");
      if (parts.length !== 3) {
        throw new Error("Token JWT mal formado");
      }
      const base64Url = parts[1];
      const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split("")
          .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
          .join("")
      );
      const payload = JSON.parse(jsonPayload);
      if (typeof payload !== "object" || payload === null) {
        throw new Error("Payload inválido");
      }
      return payload;
    } catch (error) {
      console.error("Error parsing JWT token:", error);
      throw new Error("Invalid token format");
    }
  }

  /**
   * Almacenar token
   */
  private setToken(token: string): void {
    sessionStorage.setItem(this.TOKEN_KEY, token);
  }

  /**
   * Almacenar refresh token
   */
  private setRefreshToken(refreshToken: string): void {
    sessionStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
  }

  /**
   * Almacenar información del usuario
   */
  private setUserInfo(user: UserInfo): void {
    sessionStorage.setItem(this.USER_KEY, JSON.stringify(user));
  }

  /**
   * Limpiar datos de autenticación
   */
  private clearAuthData(): void {
    sessionStorage.removeItem('customerId');
    sessionStorage.removeItem('accountId');
    sessionStorage.removeItem(this.TOKEN_KEY);
    sessionStorage.removeItem(this.REFRESH_TOKEN_KEY);
    sessionStorage.removeItem(this.USER_KEY);
  }

  /**
   * Verificar validez del token al inicializar
   */
  private checkTokenValidity(): void {
    if (!this.hasValidToken()) {
      this.clearAuthData();
      this.isAuthenticatedSubject.next(false);
    }
  }
}
