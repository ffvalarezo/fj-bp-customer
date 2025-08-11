import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';

import { ApiRestError } from '../interfaces/api-rest-error.interface';

import { AccountRequest } from '../interfaces/account-request.interface';

import { JwtAuthService } from './jwt-auth.service';
import { map } from 'rxjs/operators';

@Injectable({
    providedIn: 'root'
})
export class AccountService {
    private baseUrl = 'http://localhost:8082/accounts';

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

    getAllAccounts(): Observable<AccountRequest[]> {
        return this.http.get<AccountRequest[]>(this.baseUrl, {
            headers: this.getDefaultHeaders()
        }).pipe(
            catchError(this.handleApiError)
        );
    }

    getAccountById(id: number): Observable<AccountRequest> {
        return this.http.get<AccountRequest>(`${this.baseUrl}/${id}`, {
            headers: this.getDefaultHeaders()
        }).pipe(
            catchError(this.handleApiError)
        );
    }

    createAccount(account: AccountRequest): Observable<AccountRequest> {
        return this.http.post<AccountRequest>(this.baseUrl, account, {
            headers: this.getDefaultHeaders()
        }).pipe(
            catchError(this.handleApiError)
        );
    }

    updateAccount(id: number, account: AccountRequest): Observable<AccountRequest> {
        return this.http.put<AccountRequest>(`${this.baseUrl}/${id}`, account, {
            headers: this.getDefaultHeaders()
        }).pipe(
            catchError(this.handleApiError)
        );
    }

    deleteAccount(id: number): Observable<boolean> {
        return this.http
            .delete<void>(`${this.baseUrl}/${id}`, {
                headers: this.getDefaultHeaders(),
            })
            .pipe(
                map(() => true),
                catchError(() => [false])
            );
    }

    getAccountsByCustomerId(customerId: number): Observable<AccountRequest[]> {
        return this.http
            .get<AccountRequest[]>(`${this.baseUrl}/filter/${customerId}`, {
                headers: this.getDefaultHeaders()
            })
            .pipe(
                map((accounts) => accounts),
                catchError((error) => {
                    console.error('Error in getAccountsByCustomerId:', error);
                    return this.handleApiError(error);
                })
            );
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
