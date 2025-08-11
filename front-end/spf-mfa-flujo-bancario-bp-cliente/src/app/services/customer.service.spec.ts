import { TestBed } from '@angular/core/testing';
import {
  provideHttpClientTesting,
  HttpTestingController,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { CustomerService } from './customer.service';
import {
  Customer,
  CustomerWithAccount,
} from '../interfaces/customer.interface';
import { ApiRestError } from '../interfaces/api-rest-error.interface';

describe('CustomerService', () => {
  let service: CustomerService;
  let httpMock: HttpTestingController;
  const token = 'test-token';
  const headers = {
    'x-guid': 'guid',
    'x-channel': 'channel',
    'x-medium': 'medium',
    'x-app': 'app',
    'x-session': 'session',
  };

  beforeEach(() => {
    // Suprimir console.error durante las pruebas
    jest.spyOn(console, 'error').mockImplementation(() => {});
    
    TestBed.configureTestingModule({
      providers: [
        CustomerService,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });
    service = TestBed.inject(CustomerService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    // Restaurar console.error después de cada prueba
    jest.restoreAllMocks();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should get all customers', () => {
    const mockCustomers: Customer[] = [{ id: 1, fullName: 'John' } as Customer];
    service.getAllCustomers().subscribe((customers) => {
      expect(customers).toEqual(mockCustomers);
    });
    const req = httpMock.expectOne('http://localhost:8081/customers');
    expect(req.request.method).toBe('GET');
    req.flush(mockCustomers);
  });

  it('should get customer by id', () => {
    const mockCustomer: Customer = { id: 1, fullName: 'John' } as Customer;
    service.getCustomerById(1).subscribe((customer) => {
      expect(customer).toEqual(mockCustomer);
    });
    const req = httpMock.expectOne('http://localhost:8081/customers/1');
    expect(req.request.method).toBe('GET');
    req.flush(mockCustomer);
  });

  it('should create a customer', () => {
    const newCustomer: Customer = { id: 2, fullName: 'Jane' } as Customer;
    service.createCustomer(newCustomer).subscribe((customer) => {
      expect(customer).toEqual(newCustomer);
    });
    const req = httpMock.expectOne('http://localhost:8081/customers');
    expect(req.request.method).toBe('POST');
    req.flush(newCustomer);
  });

  it('should update a customer', () => {
    const updatedCustomer: Customer = {
      id: 1,
      fullName: 'John Updated',
    } as Customer;
    service.updateCustomer(1, updatedCustomer).subscribe((customer) => {
      expect(customer).toEqual(updatedCustomer);
    });
    const req = httpMock.expectOne('http://localhost:8081/customers/1');
    expect(req.request.method).toBe('PUT');
    req.flush(updatedCustomer);
  });

  it('should delete a customer', () => {
    service.deleteCustomer(1).subscribe((response) => {
      expect(response).toBeUndefined();
    });
    const req = httpMock.expectOne('http://localhost:8081/customers/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('should create customer with account', () => {
    const data: CustomerWithAccount = {
      customer: { fullName: 'John' },
      account: {},
    } as CustomerWithAccount;
    service.createCustomerWithAccount(data).subscribe((result) => {
      expect(result).toEqual(data);
    });
    const req = httpMock.expectOne('http://localhost:8081/customers/account');
    expect(req.request.method).toBe('POST');
    req.flush(data);
  });

  it('should handle API errors', () => {
    const errorResponse = { status: 400, error: { message: 'Bad Request' } };
    service.getAllCustomers().subscribe({
      next: () => fail('should have failed with an error'),
      error: (err: ApiRestError) => {
        expect(err.message).toBe('Bad Request');
        expect(err.status).toBe(400);
      },
    });
    const req = httpMock.expectOne('http://localhost:8081/customers');
    req.flush(errorResponse.error, { status: 400, statusText: 'Bad Request' });
  });
});
