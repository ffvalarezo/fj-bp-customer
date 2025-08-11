import { ComponentFixture, TestBed } from "@angular/core/testing";
import { ReactiveFormsModule } from "@angular/forms";
import { Router } from "@angular/router";
import { of, throwError } from "rxjs";

import { TestJwtLoginComponent } from "./test-jwt-login.component";
import { JwtAuthService } from "../../services/jwt-auth.service";

describe("TestJwtLoginComponent", () => {
  let component: TestJwtLoginComponent;
  let fixture: ComponentFixture<TestJwtLoginComponent>;
  let mockJwtAuthService: any;
  let mockRouter: any;

  beforeEach(async () => {
    // Suprimir console.error durante las pruebas
    jest.spyOn(console, "error").mockImplementation(() => {});

    const jwtAuthServiceSpy = {
      login: jest.fn(),
      logout: jest.fn(),
      isAuthenticated: jest.fn(),
      getUserInfo: jest.fn(),
      getToken: jest.fn(),
    };

    const routerSpy = {
      navigate: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [TestJwtLoginComponent, ReactiveFormsModule],
      providers: [
        { provide: JwtAuthService, useValue: jwtAuthServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TestJwtLoginComponent);
    component = fixture.componentInstance;
    mockJwtAuthService = TestBed.inject(JwtAuthService) as any;
    mockRouter = TestBed.inject(Router) as any;

    mockJwtAuthService.isAuthenticated.mockReturnValue(false);
    fixture.detectChanges();
  });

  afterEach(() => {
    // Restaurar console.error después de cada prueba
    jest.restoreAllMocks();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });

  it("should initialize form with empty values", () => {
    expect(component.testLoginForm.get("username")?.value).toBe("");
    expect(component.testLoginForm.get("password")?.value).toBe("");
    expect(component.testLoginForm.valid).toBeFalsy();
  });

  it("should validate required fields", () => {
    const usernameControl = component.testLoginForm.get("username");
    const passwordControl = component.testLoginForm.get("password");

    expect(usernameControl?.hasError("required")).toBeTruthy();
    expect(passwordControl?.hasError("required")).toBeTruthy();
  });

  it("should enable submit button when form is valid", () => {
    component.testLoginForm.get("username")?.setValue("testuser");
    component.testLoginForm.get("password")?.setValue("testpass");

    expect(component.canSubmit()).toBeTruthy();
  });

  it("should disable submit button when form is invalid", () => {
    expect(component.canSubmit()).toBeFalsy();
  });

  it("should call login service on form submit", () => {
    const mockResponse = {
      token: "fake-token",
      user: { username: "testuser" },
    };
    mockJwtAuthService.login.mockReturnValue(of(mockResponse));

    component.testLoginForm.get("username")?.setValue("testuser");
    component.testLoginForm.get("password")?.setValue("testpass");

    component.onTestSubmit();

    expect(mockJwtAuthService.login).toHaveBeenCalledWith({
      username: "testuser",
      password: "testpass",
    });
  });

  it("should handle login success", () => {
    const mockResponse = {
      token: "fake-token",
      user: { username: "testuser" },
    };
    mockJwtAuthService.login.mockReturnValue(of(mockResponse));
    mockJwtAuthService.isAuthenticated.mockReturnValue(true);
    mockJwtAuthService.getUserInfo.mockReturnValue({
      username: "testuser",
      roles: ["user"],
    });
    // Mock válido de JWT token con estructura base64 correcta
    const mockToken =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjk5OTk5OTk5OTl9.L8i6g3PfcHlioHCCPURC9pmXT7gdJpx3kOoyAfNUwCc";
    mockJwtAuthService.getToken.mockReturnValue(mockToken);

    component.testLoginForm.get("username")?.setValue("testuser");
    component.testLoginForm.get("password")?.setValue("testpass");

    component.onTestSubmit();

    expect(component.isLoading).toBeFalsy();
    expect(component.successMessage).toBe("¡Login exitoso!");
    expect(component.errorMessage).toBe("");
  });

  it("should handle login error", () => {
    const mockError = { error: { message: "Invalid credentials" } };
    mockJwtAuthService.login.mockReturnValue(throwError(() => mockError));

    component.testLoginForm.get("username")?.setValue("testuser");
    component.testLoginForm.get("password")?.setValue("testpass");

    component.onTestSubmit();

    expect(component.isLoading).toBeFalsy();
    expect(component.errorMessage).toBe("Invalid credentials");
    expect(component.successMessage).toBe("");
  });

  it("should navigate to dashboard after successful login", (done) => {
    const mockResponse = {
      token: "fake-token",
      user: { username: "testuser" },
    };
    mockJwtAuthService.login.mockReturnValue(of(mockResponse));
    mockJwtAuthService.isAuthenticated.mockReturnValue(true);
    mockJwtAuthService.getUserInfo.mockReturnValue({
      username: "testuser",
      roles: ["user"],
    });
    // Mock válido de JWT token con estructura base64 correcta
    const mockToken =
      "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjk5OTk5OTk5OTl9.L8i6g3PfcHlioHCCPURC9pmXT7gdJpx3kOoyAfNUwCc";
    mockJwtAuthService.getToken.mockReturnValue(mockToken);

    component.testLoginForm.get("username")?.setValue("testuser");
    component.testLoginForm.get("password")?.setValue("testpass");

    component.onTestSubmit();

    setTimeout(() => {
      expect(mockRouter.navigate).toHaveBeenCalledWith(["/home"]);
      done();
    }, 3600);
  });

  it("should logout and clear data", () => {
    component.successMessage = "Success";
    component.errorMessage = "Error";
    component.tokenInfo = { username: "test" };

    component.onLogout();

    expect(mockJwtAuthService.logout).toHaveBeenCalled();
    expect(component.successMessage).toBe("");
    expect(component.errorMessage).toBe("");
    expect(component.tokenInfo).toBeNull();
  });

  it("should navigate to microfrontend", () => {
    component.goToMicrofrontend("cliente");
    expect(mockRouter.navigate).toHaveBeenCalledWith([
      "/spf-shell-flujo-bancario-bp-front/cliente",
    ]);
  });

  it("should navigate to dashboard", () => {
    component.goToDashboard();
    expect(mockRouter.navigate).toHaveBeenCalledWith(["/home"]);
  });

  it("should get field error message for required field", () => {
    const usernameControl = component.testLoginForm.get("username");
    usernameControl?.markAsTouched();

    const errorMessage = component.getFieldErrorMessage("username");
    expect(errorMessage).toBe("Usuario es requerido");
  });

  it("should update form value on field change", () => {
    const mockEvent = { target: { value: "newvalue" } };

    component.onFieldChange("username", mockEvent);

    expect(component.testLoginForm.get("username")?.value).toBe("newvalue");
    expect(component.testLoginForm.get("username")?.touched).toBeTruthy();
  });

  it("should check if field is invalid", () => {
    const usernameControl = component.testLoginForm.get("username");
    usernameControl?.markAsTouched();

    expect(component.isFieldInvalid("username")).toBeTruthy();
  });

  it("should load token info when authenticated", () => {
    mockJwtAuthService.isAuthenticated.mockReturnValue(true);
    mockJwtAuthService.getUserInfo.mockReturnValue({
      username: "testuser",
      roles: ["user"],
    });
    mockJwtAuthService.getToken.mockReturnValue(
      "header.eyJleHAiOjE2MDAwMDAwMDAsInVzZXJuYW1lIjoidGVzdHVzZXIifQ.signature"
    );

    component.loadTokenInfo();

    expect(component.tokenInfo).toBeTruthy();
    expect(component.tokenInfo.username).toBe("testuser");
  });
});
