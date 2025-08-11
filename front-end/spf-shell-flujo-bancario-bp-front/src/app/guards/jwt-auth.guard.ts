import { inject } from "@angular/core";
import { Router } from "@angular/router";
import { JwtAuthService } from "../services/jwt-auth.service";

export const jwtAuthGuard = () => {
  const jwtAuthService = inject(JwtAuthService);
  const router = inject(Router);

  if (jwtAuthService.isAuthenticated()) {
    return true;
  } else {
    // Redirigir a login si no está autenticado
    router.navigate(["/auth/login"]);
    return false;
  }
};
