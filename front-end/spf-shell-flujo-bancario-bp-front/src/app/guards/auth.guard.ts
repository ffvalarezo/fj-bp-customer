import { inject } from "@angular/core";
import { Router } from "@angular/router";
import { LoginService } from "../services/login.service";
import { JwtAuthService } from "../services/jwt-auth.service";

export const authGuard = async () => {
  const loginService = inject(LoginService);
  const jwtAuthService = inject(JwtAuthService);
  const router = inject(Router);
  try {
    // Verificar autenticación JWT primero
    if (jwtAuthService.isAuthenticated()) {
      return true;
    }

    const tokenResponse = await loginService.getToken();
    if (tokenResponse && loginService.isAuthenticated()) {
      return true;
    }

    const currentUrl = window.location.pathname + window.location.search;
    sessionStorage.setItem("redirectUrl", currentUrl);

    return router.navigate(["/auth/test-jwt"]);
  } catch (error) {
    console.error("Error en authGuard:", error);
    sessionStorage.setItem("redirectUrl", window.location.pathname);
    return router.navigate(["/auth/test-jwt"]);
  }
};
