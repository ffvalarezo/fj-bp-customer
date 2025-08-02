import { Injectable, inject } from '@angular/core';
import { HttpService } from '@pichincha/angular-sdk/http';
import { Router } from '@angular/router';
import { StorageService } from '@pichincha/angular-sdk/storage';
import { BehaviorSubject, Observable, from } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { EventBusService } from '@pichincha/angular-sdk/eventbus';

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
  providedIn: 'root'
})
export class JwtAuthService {
  private readonly TOKEN_KEY = 'jwt_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private readonly USER_KEY = 'user_info';

  private http: HttpService = inject(HttpService);
  private router = inject(Router);
  private storageService = inject(StorageService);

  // Subject para manejar el estado de autenticación
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.hasValidToken());
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private eventBus: EventBusService) {
    // Verificar token al inicializar el servicio
    this.checkTokenValidity();
  }


  private headers = {
    'Content-Type': 'application/json',
    'x-guid': '123e4567-e89b-12d3-a456-426614174000',
    'x-channel': '01',
    'x-medium': '01',
    'x-app': '12345',
    'x-session': 'session123'
  };

  login(credentials: LoginRequest): Observable<LoginResponse> {
    console.log('Iniciando proceso de login');
    console.log('Credenciales:', { username: credentials.username, password: credentials.password });
    console.log('Headers enviados:', this.headers);

    // Convertir Promise a Observable para mantener la API consistente
    const loginPromise = this.http.post<LoginResponse>('/auth/login', credentials, this.headers);

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

          // Actualizar estado de autenticación
          this.isAuthenticatedSubject.next(true);

          // Verificar que se guardó correctamente
          const savedToken = this.getToken();
          this.emitToken();
        } else {
          console.error('Response sin token válido:', response);
          throw new Error('Response inválida del servidor');
        }
      }),
      catchError((error: any) => {
        console.error('Error en login request:', error);
        console.error('Error status:', error.status);
        console.error('Error message:', error.message);
        console.error('Error body:', error.error);

        this.clearAuthData();
        this.isAuthenticatedSubject.next(false);
        throw error;
      })
    );
  }

  emitToken(): void {
    const token = this.getToken() || '';
    this.eventBus.emit('authToken', token, {
      source: 'JwtAuthService',
      timestamp: new Date().toISOString(),
    });
  }

  /**
   * Realizar logout
   */
  logout(): void {
    this.eventBus.emit('authToken', '', { source: 'JwtAuthService' });
    this.clearAuthData();
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/auth/test-jwt']);
  }

  /**
   * Obtener token JWT actual
   */
  getToken(): string | null {
    return this.storageService.get(this.TOKEN_KEY);
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
        roles: payload.roles ? [payload.roles] : []
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
        return typeof payload.roles === 'string' ? [payload.roles] : payload.roles;
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
    const refreshToken = this.storageService.get(this.REFRESH_TOKEN_KEY);

    if (!refreshToken) {
      this.logout();
      throw new Error('No refresh token available');
    }

    const refreshPromise = this.http.post<LoginResponse>('/auth/refresh', { refreshToken }, this.headers);

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
   * Decodificar token JWT
   */
  private decodeToken(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('Error parsing JWT token:', error);
      throw new Error('Invalid token format');
    }
  }

  /**
   * Almacenar token
   */
  private setToken(token: string): void {
    this.storageService.set(this.TOKEN_KEY, token);
  }

  /**
   * Almacenar refresh token
   */
  private setRefreshToken(refreshToken: string): void {
    this.storageService.set(this.REFRESH_TOKEN_KEY, refreshToken);
  }

  /**
   * Almacenar información del usuario
   */
  private setUserInfo(user: UserInfo): void {
    this.storageService.set(this.USER_KEY, JSON.stringify(user));
  }

  /**
   * Limpiar datos de autenticación
   */
  private clearAuthData(): void {
    this.storageService.remove(this.TOKEN_KEY);
    this.storageService.remove(this.REFRESH_TOKEN_KEY);
    this.storageService.remove(this.USER_KEY);
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
