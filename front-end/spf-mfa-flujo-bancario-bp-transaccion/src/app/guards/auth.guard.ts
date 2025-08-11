import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { JwtAuthService } from '../services/jwt-auth.service';

export const authGuard = async () => {
  const jwtAuthService = inject(JwtAuthService);
  const router = inject(Router);
  try {
    if (jwtAuthService.isAuthenticated()) {
      return true;
    }

    const currentUrl = window.location.pathname + window.location.search;
    sessionStorage.setItem('redirectUrl', currentUrl);

    return router.navigate(['/auth/test-jwt']);
  } catch (error) {
    console.error('Error en authGuard:', error);
    sessionStorage.setItem('redirectUrl', window.location.pathname);
    return router.navigate(['/auth/test-jwt']);
  }
};
