import { TestBed } from '@angular/core/testing';
import {
    provideHttpClientTesting,
    HttpTestingController,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { AccountService } from './account.service';
import { AccountRequest } from '../interfaces/account-request.interface';
import { ApiRestError } from '../interfaces/api-rest-error.interface';

describe('AccountService', () => {
    let service: AccountService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        jest.spyOn(console, 'error').mockImplementation(() => { });
        TestBed.configureTestingModule({
            providers: [
                AccountService,
                provideHttpClient(),
                provideHttpClientTesting(),
                { provide: 'JwtAuthService', useValue: { getToken: () => 'fake-token' } }
            ],
        });
        service = TestBed.inject(AccountService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
        jest.restoreAllMocks();
    });

    it('should be created', () => {
        expect(service).toBeTruthy();
    });

    it('should get all accounts', () => {
        const mockAccounts: AccountRequest[] = [{ accountNumber: 'ACC001', accountType: 'SAVINGS', initialBalance: 1000, status: true, customerId: 101 }];
        service.getAllAccounts().subscribe((accounts) => {
            expect(accounts).toEqual(mockAccounts);
        });
        const req = httpMock.expectOne('http://localhost:8082/accounts');
        expect(req.request.method).toBe('GET');
        req.flush(mockAccounts);
    });

    it('should get accounts by customer id', () => {
        const mockAccounts: AccountRequest[] = [{ accountNumber: 'ACC002', accountType: 'CURRENT', initialBalance: 2000, status: true, customerId: 102 }];
        service.getAccountsByCustomerId(102).subscribe((accounts) => {
            expect(accounts).toEqual(mockAccounts);
        });
        const req = httpMock.expectOne('http://localhost:8082/accounts/filter/102');
        expect(req.request.method).toBe('GET');
        req.flush(mockAccounts);
    });

    it('should create an account', () => {
        const newAccount: AccountRequest = { accountNumber: 'ACC003', accountType: 'SAVINGS', initialBalance: 3000, status: true, customerId: 103 };
        service.createAccount(newAccount).subscribe((account) => {
            expect(account).toEqual(newAccount);
        });
        const req = httpMock.expectOne('http://localhost:8082/accounts');
        expect(req.request.method).toBe('POST');
        req.flush(newAccount);
    });

    it('should update an account', () => {
        const updatedAccount: AccountRequest = { accountNumber: 'ACC004', accountType: 'CURRENT', initialBalance: 4000, status: true, customerId: 104 };
        service.updateAccount(4, updatedAccount).subscribe((account) => {
            expect(account).toEqual(updatedAccount);
        });
        const req = httpMock.expectOne('http://localhost:8082/accounts/4');
        expect(req.request.method).toBe('PUT');
        req.flush(updatedAccount);
    });

    it('should delete an account', () => {
        service.deleteAccount(5).subscribe((response) => {
            expect(response).toBeUndefined();
        });
        const req = httpMock.expectOne('http://localhost:8082/accounts/5');
        expect(req.request.method).toBe('DELETE');
        req.flush(null);
    });

    it('should handle API errors', () => {
        const errorResponse = { status: 400, error: { message: 'Bad Request' } };
        service.getAllAccounts().subscribe({
            next: () => fail('should have failed with an error'),
            error: (err: ApiRestError) => {
                expect(err.message).toBe('Bad Request');
                expect(err.status).toBe(400);
            },
        });
        const req = httpMock.expectOne('http://localhost:8082/accounts');
        req.flush(errorResponse.error, { status: 400, statusText: 'Bad Request' });
    });
});
