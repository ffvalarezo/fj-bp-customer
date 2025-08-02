import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { JwtAuthService, LoginRequest } from '../../services/jwt-auth.service';
import { 
  PichinchaButtonModule, 
  PichinchaTypographyModule,
  PichinchaInputModule
} from '@pichincha/ds-angular';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-jwt-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    PichinchaButtonModule,
    PichinchaTypographyModule,
    PichinchaInputModule
  ],
  template: `
    <div class="login-container">
      <div class="login-card">
        <!-- Logo de Banco Pichincha -->
        <div class="logo-container">
          <svg
            width="240"
            height="70"
            viewBox="0 0 220 48"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              fill-rule="evenodd"
              clip-rule="evenodd"
              d="M134.181 28.0655H130.117V47.6553H134.205V28.0697L134.181 28.0655ZM112.948 28.0655H108.86V47.6553H112.948V39.5493H121.03V47.6553L125.118 47.6553V28.0655H123.127C121.978 28.0655 121.046 29.0123 121.046 30.1803V35.7602H112.948V28.0655ZM83.3141 28.0655H79.2629V47.6553H83.3509L83.3141 28.0655ZM67.5467 31.8962C70.3797 31.8962 71.7287 32.7272 71.7287 34.7173C71.7287 36.7074 70.3265 37.6257 67.5467 37.6257H64.5461V31.8962H67.5467ZM68.2621 28.0655H60.4581V47.6553H64.5461V41.4522H68.258C70.486 41.4522 72.301 40.8373 73.7032 39.5784C75.0958 38.3336 75.8631 36.5177 75.7922 34.6342C75.7922 32.6178 75.1108 31.0251 73.7482 29.8562C72.3705 28.6513 70.5596 28.0655 68.2744 28.0655H68.2621ZM72.1171 14.6581C72.1171 16.32 70.9356 16.9183 68.6586 16.9183H64.5461V12.4934H68.7486C70.9765 12.4934 72.1007 13.2205 72.1007 14.6456L72.1171 14.6581ZM71.6347 6.99253C71.6347 8.33452 70.5636 9.00344 68.4461 9.00344H64.5461V5.01486H68.2825C70.674 5.01486 71.6347 5.54667 71.6347 7.02577V6.99253ZM73.7809 10.5739C75.0353 9.65639 75.7731 8.17682 75.7595 6.60614C75.7595 3.11613 73.1472 1.20494 69.1083 1.20494L60.4581 1.20494V20.7448H69.5825C71.5897 20.7448 73.184 20.213 74.39 19.1826C75.6021 18.1608 76.2824 16.6276 76.2337 15.0279C76.2337 13.2122 75.4161 11.7331 73.7809 10.5615V10.5739ZM89.724 13.2122H84.9411L87.3326 6.81803L89.724 13.2122ZM92.5856 20.7531H96.8167L89.7976 2.522C89.4872 1.70301 88.7112 1.16404 87.8477 1.16753L85.3949 1.16753L77.8484 20.7448H82.0795L83.5921 16.8892H91.0404L92.5856 20.7531ZM97.1315 27.7664C94.2413 27.7664 91.8498 28.6887 89.9284 30.5625C88.0071 32.4363 87.0301 34.8586 87.0301 37.904C87.0301 40.9495 87.9948 43.3509 89.9203 45.1956C91.8457 47.0404 94.3189 47.9627 97.2786 47.9627C100.525 47.9627 103.137 46.7329 105.115 44.2733L102.393 41.34C101.21 43.0916 99.2375 44.1183 97.1478 44.0697C95.5822 44.117 94.0645 43.5174 92.9413 42.4078C91.8171 41.2902 91.2366 39.757 91.2366 37.8375C91.2366 35.918 91.8171 34.4306 92.9413 33.3421C93.9992 32.2709 95.4338 31.6728 96.9271 31.6802C98.3264 31.6696 99.689 32.1346 100.798 33.0014C101.629 33.644 102.802 33.5548 103.529 32.7937L104.792 31.4849C102.958 29.0861 100.111 27.7164 97.1233 27.7955L97.1315 27.7664ZM112.866 20.7448H116.582V1.16754L112.494 1.16754V14.4337L105.34 2.19793C104.967 1.55829 104.289 1.16649 103.558 1.16754L99.8173 1.16754V20.7448H103.905V7.53681L111.076 19.7186C111.452 20.3542 112.128 20.7434 112.858 20.7448H112.866ZM126.197 15.4932C125.073 14.3756 124.496 12.8424 124.496 10.9229C124.496 9.00344 125.073 7.51603 126.197 6.42748C127.255 5.35633 128.69 4.75818 130.183 4.76557C131.598 4.75585 132.975 5.2316 134.091 6.11587C134.919 6.73568 136.07 6.63427 136.781 5.87905L138.044 4.5703C136.216 2.16606 133.372 0.788659 130.383 0.860091C127.497 0.860091 125.11 1.78245 123.184 3.6521C121.259 5.52174 120.298 7.95228 120.298 10.9977C120.298 14.0432 121.263 16.4446 123.184 18.2894C125.106 20.1341 127.583 21.0523 130.522 21.0523C133.763 21.0523 136.373 19.8238 138.355 17.367L135.632 14.4337C134.457 16.1822 132.495 17.2115 130.412 17.1717C128.846 17.219 127.328 16.6194 126.205 15.5098L126.197 15.4932ZM142.946 28.0821H139.205V47.6719H143.293V34.4389L150.467 46.6249C150.841 47.2639 151.518 47.6555 152.25 47.6553H155.966V28.0655H151.906V41.3608L144.728 29.1125C144.354 28.4741 143.677 28.0828 142.946 28.0821ZM156.117 10.9437C156.163 12.5628 155.604 14.1395 154.551 15.3561C153.505 16.5627 151.975 17.2201 150.394 17.1426C148.607 17.1426 147.205 16.5568 146.163 15.3561C145.109 14.1404 144.549 12.5631 144.597 10.9437C144.554 9.33251 145.113 7.76453 146.163 6.55628C147.244 5.35401 148.793 4.69986 150.394 4.76973C151.954 4.72933 153.451 5.39435 154.482 6.58536C155.559 7.76965 156.145 9.33103 156.117 10.9437ZM157.343 18.2021C159.263 16.3 160.31 13.6671 160.23 10.9437C160.31 8.23494 159.262 5.61751 157.343 3.73519C155.471 1.83528 152.916 0.796457 150.271 0.860091C147.628 0.809697 145.078 1.85685 143.211 3.75981C141.344 5.66276 140.321 8.25704 140.378 10.9437C140.304 13.6378 141.32 16.2449 143.19 18.1558C145.06 20.0666 147.62 21.1137 150.271 21.0523C152.911 21.1133 155.462 20.0851 157.343 18.2021ZM165.605 42.4078C164.477 41.2818 163.884 39.7487 163.884 37.8375C163.884 35.9263 164.461 34.4306 165.589 33.3421C166.646 32.2694 168.081 31.6709 169.575 31.6802C170.99 31.6678 172.368 32.1439 173.483 33.0305C174.311 33.6528 175.466 33.5513 176.177 32.7937L177.44 31.4849C175.605 29.0849 172.756 27.7151 169.767 27.7955C166.881 27.7955 164.489 28.7178 162.564 30.5916C160.638 32.4654 159.678 34.896 159.678 37.9414C159.678 40.9869 160.638 43.3883 162.564 45.233C164.489 47.0778 166.962 48.0001 169.922 48.0001C173.163 48.0001 175.775 46.7703 177.759 44.3107L175.036 41.3774C173.849 43.108 171.887 44.1189 169.812 44.0697C168.246 44.117 166.728 43.5174 165.605 42.4078ZM193.69 47.6636H197.757V28.0655H195.771C194.621 28.0655 193.69 29.0123 193.69 30.1803V35.7602H185.608V28.0655H181.52V47.6553H185.608V39.5493H193.69V47.6636ZM212.903 40.1185H208.12L210.512 33.7202L212.903 40.1185ZM220 47.6636L212.981 29.4117C212.671 28.6009 211.903 28.0664 211.047 28.0655H208.562L201.028 47.6553H205.263L206.775 43.7996H214.224L215.765 47.6553L220 47.6636Z"
              fill="#0F265C"
            />
            <path
              fill-rule="evenodd"
              clip-rule="evenodd"
              d="M47.023 0V48H15.7441C7.0346 48 0 40.8192 0 31.9288L0 0L47.023 0ZM39.1928 7.99288L23.5324 7.99288V15.2164H32.1163V23.9786H39.1928V7.99288Z"
              fill="#FFDD00"
            />
            <path
              fill-rule="evenodd"
              clip-rule="evenodd"
              d="M39.1955 8.00002H23.5352V15.2235H32.119V23.9858H39.1955V8.00002Z"
              fill="#0F265C"
            />
          </svg>
        </div>

        <div class="login-content">
          <pichincha-typography
            variant="h2"
            color="bp-primary-blue600"
            weight="600"
            size="24px"
            line-height="32px"
            category="text"
            class="login-title"
          >
            Iniciar Sesión
          </pichincha-typography>

          <pichincha-typography
            variant="body"
            color="bp-neutral-gray500"
            weight="400"
            size="16px"
            line-height="24px"
            category="text"
            class="login-subtitle"
          >
            Ingresa tus credenciales para acceder al sistema
          </pichincha-typography>

          <form [formGroup]="loginForm" (ngSubmit)="onSubmit()" class="login-form">
            <!-- Campo Usuario -->
            <div class="form-field">
              <pichincha-input
                label="Usuario"
                placeholder="Ingresa tu usuario"
                [value]="loginForm.get('username')?.value"
                (valueChange)="loginForm.get('username')?.setValue($event)"
              ></pichincha-input>
              <div *ngIf="isFieldInvalid('username')" class="field-error">
                <pichincha-typography
                  variant="caption"
                  color="bp-semantic-error500"
                  weight="400"
                  size="12px"
                  line-height="16px"
                  category="text"
                >
                  {{ getFieldErrorMessage('username') }}
                </pichincha-typography>
              </div>
            </div>

            <!-- Campo Contraseña -->
            <div class="form-field">
              <pichincha-input
                label="Contraseña"
                type="password"
                placeholder="Ingresa tu contraseña"
                [value]="loginForm.get('password')?.value"
                (valueChange)="loginForm.get('password')?.setValue($event)"
              ></pichincha-input>
              <div *ngIf="isFieldInvalid('password')" class="field-error">
                <pichincha-typography
                  variant="caption"
                  color="bp-semantic-error500"
                  weight="400"
                  size="12px"
                  line-height="16px"
                  category="text"
                >
                  {{ getFieldErrorMessage('password') }}
                </pichincha-typography>
              </div>
            </div>

            <!-- Mensaje de error general -->
            <div *ngIf="errorMessage" class="error-message">
              <pichincha-typography
                variant="body"
                color="bp-semantic-error500"
                weight="400"
                size="14px"
                line-height="20px"
                category="text"
              >
                {{ errorMessage }}
              </pichincha-typography>
            </div>

            <!-- Botón de login -->
            <pichincha-button
              size="large"
              color="primary"
              [loading]="isLoading"
              [disabled]="loginForm.invalid"
              type="submit"
              label="Iniciar Sesión"
              customWidth="100%"
            ></pichincha-button>
          </form>
        </div>
      </div>
    </div>
  `,
  styleUrls: ['./jwt-login.component.scss']
})
export class JwtLoginComponent {
  private fb = inject(FormBuilder);
  private jwtAuthService = inject(JwtAuthService);
  private router = inject(Router);

  loginForm: FormGroup;
  isLoading = false;
  errorMessage = '';

  constructor() {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';

      const credentials: LoginRequest = {
        username: this.loginForm.get('username')?.value,
        password: this.loginForm.get('password')?.value
      };

      this.jwtAuthService.login(credentials).subscribe({
        next: (response) => {
          this.isLoading = false;
          // Redirigir a la página de inicio después del login exitoso
          this.router.navigate(['/home']);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = error.error?.message || 'Error al iniciar sesión. Verifica tus credenciales.';
          console.error('Login error:', error);
        }
      });
    }
  }

  isFieldInvalid(fieldName: string): boolean {
    const field = this.loginForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }

  getFieldErrorMessage(fieldName: string): string {
    const field = this.loginForm.get(fieldName);
    
    if (field?.errors) {
      if (field.errors['required']) {
        return `${fieldName === 'username' ? 'Usuario' : 'Contraseña'} es requerido`;
      }
      if (field.errors['minlength']) {
        const minLength = field.errors['minlength'].requiredLength;
        return `Mínimo ${minLength} caracteres`;
      }
    }
    
    return '';
  }
}
