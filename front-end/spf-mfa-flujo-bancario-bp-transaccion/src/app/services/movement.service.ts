import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MovementResponse } from '../interfaces/movement-response.interface';
import { MovementRequest } from '../interfaces/movement-request.interface';
import { JwtAuthService } from './jwt-auth.service';
import { ErrorResponse } from '../interfaces/error-response.interface';
import { ReportResponse } from '../interfaces/report-response.interface';

@Injectable({
    providedIn: 'root'
})
export class MovementService {
    private baseUrl = 'http://localhost:8080/movements';

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

    getAllMovements(): Observable<MovementResponse[]> {
        return this.http.get<MovementResponse[]>(
            `${this.baseUrl}`,
            { headers: this.getDefaultHeaders() }
        );
    }

    createMovement(movement: MovementRequest): Observable<MovementResponse | ErrorResponse> {
        return this.http.post<MovementResponse>(
            `${this.baseUrl}`,
            movement,
            { headers: this.getDefaultHeaders() }
        );
    }

    getMovementById(id: number): Observable<MovementResponse> {
        return this.http.get<MovementResponse>(
            `${this.baseUrl}/${id}`,
            { headers: this.getDefaultHeaders() }
        );
    }
    
    getMovementsByAccountNumber(accountNumber: string): Observable<MovementResponse[]> {
        return this.http.get<MovementResponse[]>(
            `${this.baseUrl}/account/${accountNumber}`,
            { headers: this.getDefaultHeaders() }
        );
    }

    deleteMovement(id: number): Observable<void> {
        return this.http.delete<void>(
            `${this.baseUrl}/${id}`,
            { headers: this.getDefaultHeaders() }
        );
    }

    movementsReportGet(params: {
        startDate: string;
        endDate: string;
        customerId: string;
    }): Observable<ReportResponse> {
        const httpParams = new HttpParams()
            .set('startDate', params.startDate)
            .set('endDate', params.endDate)
            .set('customerId', params.customerId);

        return this.http.get<ReportResponse>(
            `${this.baseUrl}/report`,
            {
                headers: this.getDefaultHeaders(),
                params: httpParams
            }
        );
    }
}
