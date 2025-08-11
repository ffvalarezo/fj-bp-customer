import { TestBed } from "@angular/core/testing";
import { Router } from "@angular/router";
import { authGuard } from "./auth.guard";
import { LoginService } from "../services/login.service";
import { JwtAuthService } from "../services/jwt-auth.service";

describe("authGuard", () => {
  let mockLoginService: any;
  let mockJwtAuthService: any;
  let mockRouter: any;

  beforeEach(() => {
    // Suprimir console.error durante las pruebas
    jest.spyOn(console, "error").mockImplementation(() => {});

    mockLoginService = {
      getToken: jest.fn(),
      isAuthenticated: jest.fn(),
    };

    mockJwtAuthService = {
      isAuthenticated: jest.fn(),
    };

    mockRouter = {
      navigate: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        { provide: LoginService, useValue: mockLoginService },
        { provide: JwtAuthService, useValue: mockJwtAuthService },
        { provide: Router, useValue: mockRouter },
      ],
    });
  });

  afterEach(() => {
    // Restaurar console.error después de cada prueba
    jest.restoreAllMocks();
  });

  it("should return true if JWT is authenticated", async () => {
    mockJwtAuthService.isAuthenticated.mockReturnValue(true);

    const result = await TestBed.runInInjectionContext(() => authGuard());

    expect(result).toBe(true);
    expect(mockJwtAuthService.isAuthenticated).toHaveBeenCalled();
  });

  it("should return true if login service is authenticated", async () => {
    mockJwtAuthService.isAuthenticated.mockReturnValue(false);
    mockLoginService.getToken.mockResolvedValue("token");
    mockLoginService.isAuthenticated.mockReturnValue(true);

    const result = await TestBed.runInInjectionContext(() => authGuard());

    expect(result).toBe(true);
    expect(mockLoginService.getToken).toHaveBeenCalled();
    expect(mockLoginService.isAuthenticated).toHaveBeenCalled();
  });

  it("should navigate to /auth/test-jwt if not authenticated", async () => {
    mockJwtAuthService.isAuthenticated.mockReturnValue(false);
    mockLoginService.getToken.mockResolvedValue(null);
    mockLoginService.isAuthenticated.mockReturnValue(false);
    mockRouter.navigate.mockResolvedValue(true);

    const result = await TestBed.runInInjectionContext(() => authGuard());

    expect(mockRouter.navigate).toHaveBeenCalledWith(["/auth/test-jwt"]);
    expect(result).toBe(true);
  });

  it("should handle errors and navigate to /auth/test-jwt", async () => {
    mockJwtAuthService.isAuthenticated.mockReturnValue(false);
    mockLoginService.getToken.mockRejectedValue(new Error("Token error"));
    mockRouter.navigate.mockResolvedValue(true);

    const result = await TestBed.runInInjectionContext(() => authGuard());

    expect(mockRouter.navigate).toHaveBeenCalledWith(["/auth/test-jwt"]);
    expect(result).toBe(true);
  });
});
