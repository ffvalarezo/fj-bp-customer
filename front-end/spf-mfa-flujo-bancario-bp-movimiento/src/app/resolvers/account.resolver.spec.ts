import { TestBed } from '@angular/core/testing';

import { AccountResolver } from './account.resolver';
import { AccountService } from "../services/account.service";
import { ActivatedRouteSnapshot, RouterStateSnapshot } from "@angular/router";

describe('AccountResolver', () => {
  let resolver: AccountResolver;

  let mockAccountService = {
    getAccountById: jest.fn(),
  }

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AccountResolver,
        { provide: AccountService, useValue: mockAccountService }
      ]
    });
    resolver = TestBed.inject(AccountResolver);
  });

  it('should be created', () => {
    expect(resolver).toBeTruthy();
  });

  it('should resolve account data', (done) => {
    const mockAccount = { id: 1, name: 'Test Account' };
    const mockActivatedRoute = {
      params: {
        id: '1'
      }
    }

    mockAccountService.getAccountById.mockReturnValue(of(mockAccount));

    resolver.resolve(mockActivatedRoute as unknown as ActivatedRouteSnapshot, {} as RouterStateSnapshot)
      .then(() => {
        done();
        expect(mockAccountService.getAccountById).toHaveBeenCalledWith('1');
      });
  });
});
