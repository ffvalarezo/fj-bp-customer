import { inject, Injectable, signal } from "@angular/core";
import { Router } from "@angular/router";
import { AuthService } from "@pichincha/angular-sdk/auth";
import { StorageService } from "@pichincha/angular-sdk/storage";
import { JwtAuthService } from "./jwt-auth.service";
import { lastValueFrom } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class LoginService {
  router: Router = inject(Router);
  authService: AuthService = inject(AuthService);
  jwtAuthService: JwtAuthService = inject(JwtAuthService);
  loading = signal<boolean>(false);
  storageservice: StorageService = inject(StorageService);

  initialConfig() {
    // Configuración simplificada solo con Azure Auth
    this.authService.getAccountState().subscribe((isAuthenticated) => {
      if (isAuthenticated) {
        this.setLoading(false);
        this.goToHome();
      }
    });

    this.authService.getInteractionState().subscribe(() => {
      let activeAccount = this.authService.msalService.instance.getActiveAccount();

      if (!activeAccount && this.authService.isAuthenticated()) {
        let accounts = this.authService.getAllAccounts();
        this.authService.msalService.instance.setActiveAccount(accounts[0]);
        this.setLoading(false);
        this.goToHome();
      }
    });
  }

  setLoading(value: boolean) {
    this.loading.set(value);
    this.storageservice.set("loading", value);
  }

  activateAccount() {
    this.setLoading(true);
    // Simplificado - solo verificar si está autenticado
    if (this.authService.isAuthenticated()) {
      this.setLoading(false);
      this.goToHome();
    }
  }

  detectActivateAccount() {
    // Verificar autenticación de Azure
    const isAuthenticated = this.authService.isAuthenticated();

    if (isAuthenticated) {
      this.goToHome();
    }
  }

  goToHome() {
    this.router.navigate(["/spf-shell-flujo-bancario-bp-front"]);
  }

  getLoadingStorage() {
    return !!this.storageservice.get("loading");
  }

  async getToken(){
    // Usar token de Azure
    return lastValueFrom(this.authService.handleAuthRedirect())
  }

  isAuthenticated(){
    // Verificar autenticación de Azure
    return this.authService.isAuthenticated();
  }

  /**
   * Método para logout
   */
  logout() {
    // Limpiar storage y redirigir
    this.storageservice.clear();
    this.router.navigate(['/auth/login']);
  }

  /**
   * Verificar permisos del usuario (simplificado)
   */
  hasPermission(permission: string): boolean {
    return this.authService.isAuthenticated();
  }

  /**
   * Obtener permisos del usuario (simplificado)
   */
  getUserPermissions(): string[] {
    return this.authService.isAuthenticated() ? ['user'] : [];
  }
}
