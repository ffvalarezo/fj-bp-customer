import { Component, inject } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from "@angular/forms";
import { Router } from "@angular/router";
import { JwtAuthService, LoginRequest } from "../../services/jwt-auth.service";
import {
  PichinchaButtonModule,
  PichinchaTypographyModule,
  PichinchaInputModule,
} from "@pichincha/ds-angular";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-test-jwt-login",
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PichinchaButtonModule,
    PichinchaTypographyModule,
    PichinchaInputModule,
  ],
  templateUrl: "./test-jwt-login.component.html",
  styleUrls: ["./test-jwt-login.component.scss"],
})
export class TestJwtLoginComponent {
  private readonly fb = inject(FormBuilder);
  public jwtAuthService = inject(JwtAuthService);
  private readonly router = inject(Router);

  testLoginForm: FormGroup;
  isLoading = false;
  errorMessage = "";
  successMessage = "";
  tokenInfo: any = null;

  constructor() {
    this.testLoginForm = this.fb.group({
      username: ["", [Validators.required]],
      password: ["", [Validators.required]],
    });

    console.log("Form initialized:", this.testLoginForm.valid);
    console.log("Form value:", this.testLoginForm.value);
    console.log("Can submit initially:", this.canSubmit());

    // Suscribirse a cambios del formulario para debug
    this.testLoginForm.valueChanges.subscribe((value) => {
      console.log("Form value changed:", value);
      console.log("Form valid:", this.testLoginForm.valid);
      console.log("Can submit:", this.canSubmit());
    });

    // Verificar si ya está autenticado al cargar
    if (this.jwtAuthService.isAuthenticated()) {
      this.loadTokenInfo();
    }
  }

  onTestSubmit(): void {
    console.log("onTestSubmit called!");
    console.log("Form valid:", this.testLoginForm.valid);
    console.log("Form value:", this.testLoginForm.value);

    if (this.testLoginForm.valid) {
      this.isLoading = true;
      this.errorMessage = "";
      this.successMessage = "";

      const credentials: LoginRequest = {
        username: this.testLoginForm.get("username")?.value,
        password: this.testLoginForm.get("password")?.value,
      };

      console.log("Intentando login con:", credentials);
      console.log(
        "Headers que se enviarán: x-guid, x-channel, x-medium, x-app, x-session"
      );

      this.jwtAuthService.login(credentials).subscribe({
        next: (response) => {
          this.isLoading = false;
          this.successMessage = "¡Login exitoso!";
          this.loadTokenInfo();
          console.log("Login response:", response);

          // Redirigir a la página de inicio después del login exitoso
          setTimeout(() => {
            this.goToDashboard();
          }, 3500);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage =
            error.error?.message || "Error al conectar con el servidor";
          console.error("Login error:", error);
        },
      });
    }
  }

  onLogout(): void {
    this.jwtAuthService.logout();
    this.successMessage = "";
    this.errorMessage = "";
    this.tokenInfo = null;
  }

  goToMicrofrontend(type: string = "cliente"): void {
    this.router.navigate([`/spf-shell-flujo-bancario-bp-front/${type}`]);
  }

  goToDashboard(): void {
    this.router.navigate(["/home"]);
  }

  loadTokenInfo(): void {
    if (this.jwtAuthService.isAuthenticated()) {
      const userInfo = this.jwtAuthService.getUserInfo();
      const token = this.jwtAuthService.getToken();

      if (token && userInfo) {
        try {
          const payload = JSON.parse(atob(token.split(".")[1]));
          this.tokenInfo = {
            username: userInfo.username,
            roles: userInfo.roles,
            expiration: new Date(payload.exp * 1000),
          };
        } catch (error) {
          console.error("Error parsing token:", error);
        }
      }
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.testLoginForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getFieldErrorMessage(fieldName: string): string {
    const field = this.testLoginForm.get(fieldName);

    if (field?.errors) {
      if (field.errors["required"]) {
        return `${
          fieldName === "username" ? "Usuario" : "Contraseña"
        } es requerido`;
      }
      if (field.errors["minlength"]) {
        const minLength = field.errors["minlength"].requiredLength;
        return `Mínimo ${minLength} caracteres`;
      }
    }

    return "";
  }

  canSubmit(): boolean {
    const username = this.testLoginForm.get("username")?.value;
    const password = this.testLoginForm.get("password")?.value;
    const isValid =
      username && password && username.trim() !== "" && password.trim() !== "";
    return isValid;
  }

  onFieldChange(fieldName: string, event: any): void {
    const value = event.target?.value || event;
    console.log(`${fieldName} changed:`, value);
    this.testLoginForm.get(fieldName)?.setValue(value);
    this.testLoginForm.get(fieldName)?.markAsTouched();
  }
}
