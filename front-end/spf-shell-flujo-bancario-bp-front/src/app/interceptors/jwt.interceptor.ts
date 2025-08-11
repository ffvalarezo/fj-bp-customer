import { Injectable } from "@angular/core";
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse,
} from "@angular/common/http";
import { Observable, throwError, BehaviorSubject } from "rxjs";
import { catchError, filter, take, switchMap } from "rxjs/operators";
import { JwtAuthService } from "../services/jwt-auth.service";

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<any> = new BehaviorSubject<any>(
    null
  );

  constructor(private jwtAuthService: JwtAuthService) {}

  intercept(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    // Solo agregar token a requests al backend (no a assets, etc.)
    if (this.shouldAddToken(request.url)) {
      const token = this.jwtAuthService.getToken();

      if (token) {
        request = this.addTokenToRequest(request, token);
      }
    }

    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        // Si el error es 401 y tenemos un token, intentar refrescar
        if (error.status === 401 && this.jwtAuthService.getToken()) {
          return this.handle401Error(request, next);
        }

        return throwError(() => error);
      })
    );
  }

  private shouldAddToken(url: string): boolean {
    // Solo agregar token a requests del API backend
    return url.includes("/api/") && !url.includes("/auth/login");
  }

  private addTokenToRequest(
    request: HttpRequest<any>,
    token: string
  ): HttpRequest<any> {
    return request.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  private handle401Error(
    request: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      return this.jwtAuthService.refreshToken().pipe(
        switchMap((response: any) => {
          this.isRefreshing = false;
          this.refreshTokenSubject.next(response.token);
          return next.handle(this.addTokenToRequest(request, response.token));
        }),
        catchError((error) => {
          this.isRefreshing = false;
          this.jwtAuthService.logout();
          return throwError(() => error);
        })
      );
    } else {
      // Si ya estamos refrescando, esperar al nuevo token
      return this.refreshTokenSubject.pipe(
        filter((token) => token != null),
        take(1),
        switchMap((jwt) => {
          return next.handle(this.addTokenToRequest(request, jwt));
        })
      );
    }
  }
}
