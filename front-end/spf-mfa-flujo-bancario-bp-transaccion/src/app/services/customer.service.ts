import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';

import { ApiRestError } from '../interfaces/api-rest-error.interface';

import {
  Customer,
} from '../interfaces/customer.interface';

import { JwtAuthService } from './jwt-auth.service';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private apiUrl = 'http://localhost:8081/customers';

  constructor(private http: HttpClient, private jwtService: JwtAuthService) { }

  private getDefaultHeaders(): HttpHeaders {
    return new HttpHeaders({
      Authorization: `Bearer ${this.jwtService.getToken()}`,
      'x-guid': '123e4567-e89b-12d3-a456-426614174000',
      'x-channel': '01',
      'x-medium': '01',
      'x-app': '22334',
      'x-session': 'session-id-123',
    });
  }

  getCustomerById(id: number): Observable<Customer> {
    return this.http
      .get<Customer>(`${this.apiUrl}/${id}`, {
        headers: this.getDefaultHeaders(),
      })
      .pipe(catchError(this.handleApiError));
  }

  private handleApiError(error: any) {
    console.error('API error occurred:', error);
    let apiError: ApiRestError = {};
    if (error.error && typeof error.error === 'object') {
      apiError = error.error;
    } else if (typeof error === 'string') {
      apiError.message = error;
    } else {
      apiError.message = 'Unknown error';
    }
    apiError.status = error.status || apiError.status;
    return throwError(() => apiError);
  }
}
