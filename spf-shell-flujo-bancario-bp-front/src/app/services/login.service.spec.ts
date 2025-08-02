import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { AuthService } from '@pichincha/angular-sdk/auth';
import { StorageService } from '@pichincha/angular-sdk/storage';
import { LoginService } from './login.service';
import { of } from 'rxjs';

describe('LoginService', () => {
  let service: LoginService;
  let authServiceMock: any;
  let routerMock: any;
  let storageServiceMock: any;

  beforeEach(() => {
    authServiceMock = {
      initialize: jest.fn(),
      getAccountState: jest.fn().mockReturnValue({
        subscribe: jest.fn((callback) => callback(true)),
      }),
      getInteractionState: jest.fn().mockReturnValue({
        subscribe: jest.fn((callback) => callback()),
      }),
      msalService: {
        instance: {
          getActiveAccount: jest.fn(),
          setActiveAccount: jest.fn(),
        },
      },
      isAuthenticated: jest.fn().mockReturnValue(true),
      getAllAccounts: jest.fn().mockReturnValue([{}]),
      authenticate: jest.fn(),
      handleAuthRedirect: jest.fn(() => of({}))
    };

    routerMock = {
      navigate: jest.fn(),
    };

    storageServiceMock = {
      set: jest.fn(),
      get: jest.fn(() => true)
    };

    TestBed.configureTestingModule({
      providers: [
        LoginService,
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: StorageService, useValue: storageServiceMock },
      ],
    });

    service = TestBed.inject(LoginService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should initialize authService and navigate to home if authenticated', () => {
    service.initialConfig();
    expect(authServiceMock.initialize).toHaveBeenCalledWith(true);
    expect(routerMock.navigate).toHaveBeenCalledWith(['/spf-shell-flujo-bancario-bp-front']);
  });

  it('should set loading state', () => {
    service.setLoading(true);
    expect(storageServiceMock.set).toHaveBeenCalledWith('loading', true);
  });

  it('should execute getLoadingStorage and return value', () => {
    const resp = service.getLoadingStorage();
    expect(storageServiceMock.get).toHaveBeenCalledWith('loading');
    expect(resp).toBe(true);
  });

  it('should activate account and set loading state', () => {
    service.activateAccount();
    expect(authServiceMock.authenticate).toHaveBeenCalled();
    expect(storageServiceMock.set).toHaveBeenCalledWith('loading', true);
  });

  it('should detect active account and navigate to home', () => {
    service.detectActivateAccount();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/spf-shell-flujo-bancario-bp-front']);
  });

  it('should navigate to home', () => {
    service.goToHome();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/spf-shell-flujo-bancario-bp-front']);
  });

  it('should get isAutenticated', async () => {
    await service.getToken();
    expect(authServiceMock.handleAuthRedirect).toBeCalled()
  });

  it('should get isAutenticated', () => {
    service.isAuthenticated();
    expect(authServiceMock.isAuthenticated).toBeCalled()
  });
});