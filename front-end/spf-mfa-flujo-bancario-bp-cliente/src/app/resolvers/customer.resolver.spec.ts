import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { CustomerResolver } from './customer.resolver';
import { CustomerService } from '../services/customer.service';
import { ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

describe('CustomerResolver', () => {
  let resolver: CustomerResolver;

  let mockCustomerService = {
    getCustomerById: jest.fn(),
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CustomerResolver,
        { provide: CustomerService, useValue: mockCustomerService },
      ],
    });
    resolver = TestBed.inject(CustomerResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('should resolve customer by id', async () => {
    const mockCustomer = { id: 1, name: 'Test Customer' };
    const mockActivatedRoute = {
      params: {
        id: '1',
      },
    };

    mockCustomerService.getCustomerById.mockReturnValue(of(mockCustomer));

    const result = await resolver.resolve(
      mockActivatedRoute as unknown as ActivatedRouteSnapshot,
      {} as RouterStateSnapshot
    );

    expect(result).toEqual(mockCustomer);
    expect(mockCustomerService.getCustomerById).toHaveBeenCalledWith('1');
  });
});
