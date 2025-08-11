import { TestBed } from "@angular/core/testing";
import { Router } from "@angular/router";
import { AuthService } from "@pichincha/angular-sdk/auth";
import { StorageService } from "@pichincha/angular-sdk/storage";
import { LoginService } from "./login.service";
import { JwtAuthService } from "./jwt-auth.service";
import { of } from "rxjs";

describe("LoginService", () => {
  let service: LoginService;
  let authServiceMock: any;
  let routerMock: any;
  let storageServiceMock: any;
  let jwtAuthServiceMock: any;

  beforeEach(() => {
    authServiceMock = {
      getAccountState: jest.fn().mockReturnValue(of(true)),
      getInteractionState: jest.fn().mockReturnValue(of({})),
      msalService: {
        instance: {
          getActiveAccount: jest.fn(),
          setActiveAccount: jest.fn(),
        },
      },
      isAuthenticated: jest.fn().mockReturnValue(true),
      getAllAccounts: jest.fn().mockReturnValue([{}]),
      handleAuthRedirect: jest.fn(() => of("mock-token")),
    };

    routerMock = {
      navigate: jest.fn(),
    };

    storageServiceMock = {
      set: jest.fn(),
      get: jest.fn(() => true),
      clear: jest.fn(),
    };

    jwtAuthServiceMock = {
      isAuthenticated: jest.fn().mockReturnValue(false),
      getUserInfo: jest.fn().mockReturnValue(null),
      logout: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        LoginService,
        { provide: AuthService, useValue: authServiceMock },
        { provide: Router, useValue: routerMock },
        { provide: StorageService, useValue: storageServiceMock },
        { provide: JwtAuthService, useValue: jwtAuthServiceMock },
      ],
    });

    service = TestBed.inject(LoginService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });

  it("should initialize and navigate to home if authenticated", () => {
    service.initialConfig();
    expect(authServiceMock.getAccountState).toHaveBeenCalled();
    expect(authServiceMock.getInteractionState).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith([
      "/spf-shell-flujo-bancario-bp-front",
    ]);
  });

  it("should set loading state", () => {
    service.setLoading(true);
    expect(storageServiceMock.set).toHaveBeenCalledWith("loading", true);
  });

  it("should execute getLoadingStorage and return value", () => {
    const resp = service.getLoadingStorage();
    expect(storageServiceMock.get).toHaveBeenCalledWith("loading");
    expect(resp).toBe(true);
  });

  it("should activate account when authenticated", () => {
    service.activateAccount();
    expect(authServiceMock.isAuthenticated).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith([
      "/spf-shell-flujo-bancario-bp-front",
    ]);
  });

  it("should detect active account and navigate to home", () => {
    service.detectActivateAccount();
    expect(routerMock.navigate).toHaveBeenCalledWith([
      "/spf-shell-flujo-bancario-bp-front",
    ]);
  });

  it("should navigate to home", () => {
    service.goToHome();
    expect(routerMock.navigate).toHaveBeenCalledWith([
      "/spf-shell-flujo-bancario-bp-front",
    ]);
  });

  it("should get token", async () => {
    await service.getToken();
    expect(authServiceMock.handleAuthRedirect).toHaveBeenCalled();
  });

  it("should check if authenticated", () => {
    service.isAuthenticated();
    expect(authServiceMock.isAuthenticated).toHaveBeenCalled();
  });

  it("should logout and clear storage", () => {
    service.logout();
    expect(storageServiceMock.clear).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(["/auth/login"]);
  });

  it("should check permissions", () => {
    const result = service.hasPermission("test");
    expect(authServiceMock.isAuthenticated).toHaveBeenCalled();
    expect(result).toBe(true);
  });

  it("should get user permissions", () => {
    const permissions = service.getUserPermissions();
    expect(authServiceMock.isAuthenticated).toHaveBeenCalled();
    expect(permissions).toEqual(["user"]);
  });
});
