import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, from, throwError } from 'rxjs';
import { tap, catchError, map } from 'rxjs/operators';
import { LoginResponse } from '../interfaces/login-response.interface';

@Injectable({
  providedIn: 'root',
})
export class JwtAuthService {
  private readonly TOKEN_KEY = 'jwt_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private router = inject(Router);
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(
    this.hasValidToken()
  );

  private apiUrl = 'http://localhost:8080/api/';

  private getDefaultHeaders(): HttpHeaders {
    return new HttpHeaders({
      Authorization: `Bearer ${this.getToken()}`,
      'x-guid': sessionStorage.getItem('x-guid') || '',
      'x-channel': sessionStorage.getItem('x-channel') || '',
      'x-medium': sessionStorage.getItem('x-medium') || '',
      'x-app': sessionStorage.getItem('x-app') || '',
      'x-session': sessionStorage.getItem('x-session') || '',
    });
  }

  constructor(private http: HttpClient) {}

  /**
   * Realizar logout
   */
  logout(): void {
    this.clearAuthData();
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/auth/test-jwt']);
  }

  /**
   * Limpiar datos de autenticación
   */
  private clearAuthData(): void {
    sessionStorage.removeItem(this.TOKEN_KEY);
    sessionStorage.removeItem(this.REFRESH_TOKEN_KEY);
  }

  /**
   * Almacenar token
   */
  private setToken(token: string): void {
    sessionStorage.setItem(this.TOKEN_KEY, token);
  }

  /**
   * Refrescar token JWT
   */
  refreshToken(): Observable<LoginResponse> {
    const refreshToken = sessionStorage.getItem(this.REFRESH_TOKEN_KEY);

    if (!refreshToken) {
      this.logout();
      throw new Error('No refresh token available');
    }

    const refreshPromise = this.http
      .post<LoginResponse>(this.apiUrl, refreshToken, {
        headers: this.getDefaultHeaders(),
      })
      .pipe(catchError(this.handleApiError));

    return from(refreshPromise).pipe(
      tap((response: any) => {
        if (response?.token) {
          this.setToken(response.token);
          this.isAuthenticatedSubject.next(true);
        }
      }),
      catchError((error: any) => {
        console.error('Error refreshing token:', error);
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

  /**
   * Obtener token JWT actual
   */
  getToken(): string | null {
    return sessionStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Decodificar token JWT
   */
  private decodeToken(token: string): any {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) {
        throw new Error('Token JWT mal formado');
      }
      const base64Url = parts[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      const payload = JSON.parse(jsonPayload);
      if (typeof payload !== 'object' || payload === null) {
        throw new Error('Payload inválido');
      }
      return payload;
    } catch (error) {
      console.error('Error parsing JWT token:', error);
      throw new Error('Invalid token format');
    }
  }

  /**
   * Verificar si el usuario está autenticado
   */
  isAuthenticated(): boolean {
    return this.hasValidToken();
  }

  /**
   * Manejar errores de la API
   */
  private handleApiError(error: any): Observable<never> {
    console.error('API error:', error);
    return throwError(() => error);
  }

  /**
   * Valida el token actual o intenta refrescarlo si es inválido.
   * Retorna un Observable<boolean> indicando si el usuario sigue autenticado.
   */
  validateOrRefreshToken(): Observable<boolean> {
    if (this.hasValidToken()) {
      return new BehaviorSubject<boolean>(true).asObservable();
    }
    return this.refreshToken().pipe(
      tap(() => this.isAuthenticatedSubject.next(this.hasValidToken())),
      tap(() => {}),
      map(() => true),
      catchError(() => {
        this.isAuthenticatedSubject.next(false);
        return new BehaviorSubject<boolean>(false).asObservable();
      })
    );
  }
}
