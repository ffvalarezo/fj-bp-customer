import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, forkJoin } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

export interface MicrofrontendInfo {
  title: string;
  description: string;
  route: string;
  isActive: boolean;
  port: number;
  remoteEntryUrl: string;
}

@Injectable({
  providedIn: 'root'
})
export class MicrofrontendService {
  private http = inject(HttpClient);

  private microfrontends: MicrofrontendInfo[] = [
    {
      title: 'Inicio',
      description: 'Navegación centralizada.',
      route: 'home',
      isActive: true, // Siempre disponible
      port: 4200, // Puerto principal
      remoteEntryUrl: '' // No es un microfrontend externo
    },
    {
      title: 'Cliente',
      description: 'Clientes y datos personales.',
      route: 'cliente',
      isActive: false,
      port: 4201,
      remoteEntryUrl: 'http://localhost:4201/spf-mfa-flujo-bancario-bp-cliente/remoteEntry.js'
    },
    {
      title: 'Cuenta',
      description: 'Cuentas bancarias.',
      route: 'movimiento',
      isActive: false,
      port: 4202,
      remoteEntryUrl: 'http://localhost:4202/spf-mfa-flujo-bancario-bp-movimiento/remoteEntry.js'
    },
    {
      title: 'Transacción',
      description: 'Procesamiento y autorización de transacciones financieras.',
      route: 'transaccion',
      isActive: false,
      port: 4203,
      remoteEntryUrl: 'http://localhost:4203/remoteEntry.js'
    }
  ];

  /**
   * Verificar disponibilidad de todos los microfrontends
   */
  checkMicrofrontendsAvailability(): Observable<MicrofrontendInfo[]> {
    const checks = this.microfrontends.map(mf => 
      this.checkSingleMicrofrontend(mf)
    );

    return forkJoin(checks);
  }

  /**
   * Verificar disponibilidad de un microfrontend específico
   */
  private checkSingleMicrofrontend(mf: MicrofrontendInfo): Observable<MicrofrontendInfo> {
    // Página home siempre está disponible (no es un microfrontend externo)
    if (mf.route === 'home') {
      return of({ ...mf, isActive: true });
    }
    
    return this.http.get(mf.remoteEntryUrl, { 
      responseType: 'text',
      headers: { 'Cache-Control': 'no-cache' }
    }).pipe(
      map(() => ({ ...mf, isActive: true })),
      catchError(() => of({ ...mf, isActive: false }))
    );
  }

  /**
   * Obtener información básica de microfrontends (sin verificar disponibilidad)
   */
  getMicrofrontendsInfo(): MicrofrontendInfo[] {
    return [...this.microfrontends];
  }

  /**
   * Verificar si un microfrontend específico está disponible
   */
  isMicrofrontendAvailable(route: string): Observable<boolean> {
    const mf = this.microfrontends.find(m => m.route === route);
    if (!mf) {
      return of(false);
    }

    return this.http.get(mf.remoteEntryUrl, { 
      responseType: 'text',
      headers: { 'Cache-Control': 'no-cache' }
    }).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  /**
   * Obtener URL del remoteEntry para un microfrontend
   */
  getRemoteEntryUrl(route: string): string | null {
    const mf = this.microfrontends.find(m => m.route === route);
    return mf ? mf.remoteEntryUrl : null;
  }

  /**
   * Obtener información de un microfrontend específico
   */
  getMicrofrontendInfo(route: string): MicrofrontendInfo | null {
    return this.microfrontends.find(m => m.route === route) || null;
  }
}
