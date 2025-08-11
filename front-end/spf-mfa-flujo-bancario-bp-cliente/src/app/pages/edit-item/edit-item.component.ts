import { Component, inject, signal } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import {
  PichinchaButtonModule,
  PichinchaInputModule,
  PichinchaReactiveControlsModule,
  PichinchaTypographyModule,
  PichinchaCheckBoxModule,
  PichinchaInputMessageModule,
} from '@pichincha/ds-angular';
import { CustomerService } from 'src/app/services/customer.service';
import {
  Customer,
  CustomerWithAccount,
  AccountRequest,
} from 'src/app/interfaces/customer.interface';
import { Gender } from 'src/app/enums/gender.enum';

@Component({
  selector: 'app-edit-item',
  standalone: true,
  imports: [
    CommonModule,
    PichinchaTypographyModule,
    PichinchaInputModule,
    PichinchaButtonModule,
    PichinchaReactiveControlsModule,
    PichinchaCheckBoxModule,
    PichinchaInputMessageModule,
    /* ---------- */
    RouterModule,
    ReactiveFormsModule,
  ],
  providers: [],
  styleUrls: ['./edit-item.component.scss'],
  templateUrl: './edit-item.component.html',
})
export class EditItemComponent {
  router = inject(Router);
  fb = inject(FormBuilder);
  activatedRoute = inject(ActivatedRoute);
  customerService = inject(CustomerService);

  editForm = signal<boolean>(false);
  includeAccount = signal<boolean>(false);

  customerForm!: FormGroup;
  accountForm!: FormGroup;

  formErrorMessage: string = '';

  // Gender options for template
  genderOptions = [
    { value: Gender.MASCULINO, label: 'Masculino' },
    { value: Gender.FEMENINO, label: 'Femenino' },
  ];

  constructor() {
    this.buildForms();

    const { customer } = this.activatedRoute.snapshot.data;
    this.setValueForm(customer);
  }

  buildForms() {
    this.customerForm = this.fb.group({
      id: [0],
      fullName: [null, Validators.required],
      gender: [null, Validators.required],
      age: [
        null,
        [Validators.required, Validators.min(18), Validators.max(120)],
      ],
      identification: [
        null,
        [Validators.required, this.identificationValidator],
      ],
      address: [null, Validators.required],
      celular: [null, Validators.required],
      email: [null, [Validators.required, Validators.email]],
      customerId: [null],
      password: [null, [Validators.required, Validators.minLength(6)]],
      active: [true],
    });

    this.accountForm = this.fb.group({
      accountNumber: [null, Validators.required],
      accountType: ['SAVINGS', Validators.required],
      initialBalance: [0, [Validators.required, Validators.min(0)]],
      status: [true],
      customerId: [null],
    });
  }

  // Custom validator for 10-digit identification
  identificationValidator(control: FormControl) {
    const value = control.value;
    if (!value) return null;

    const isValid = /^\d{10}$/.test(value);
    return isValid ? null : { invalidIdentification: true };
  }

  setValueForm(customer?: Customer) {
    if (!customer) return;

    this.editForm.set(true);
    this.customerForm.patchValue(customer);
  }

  onIncludeAccountChange(e: Event) {
    const { detail } = e as unknown as CustomEvent;
    this.includeAccount.set(detail.checked);
    if (!this.includeAccount()) {
      this.accountForm.reset({
        accountType: 'SAVINGS',
        initialBalance: 0,
        status: true,
      });
    }
  }

  onSubmit() {
    try {
      this.customerForm.markAllAsTouched();

      if (this.includeAccount()) {
        this.accountForm.markAllAsTouched();
      }

      if (
        this.customerForm.invalid ||
        (this.includeAccount() && this.accountForm.invalid)
      ) {
        throw new Error('Form validation failed');
      }

      const customerData = this.customerForm.getRawValue() as Customer;

      if (this.includeAccount()) {
        const accountData = this.accountForm.getRawValue() as AccountRequest;
        const customerWithAccount: CustomerWithAccount = {
          customer: customerData,
          account: accountData,
        };
        this.getCreateWithAccount(customerWithAccount).subscribe({
          next: () => this.navigateListPage(),
          error: (err) => {
            this.formErrorMessage =
              typeof err === 'string'
                ? err.split(':')[0]
                : err?.detail?.split(':')[0] || 'Error al enviar el formulario';
            console.error('Error submitting form:', err)
          },
        });
      } else {
        this.getEditOrCreateCustomer(customerData).subscribe({
          next: () => this.navigateListPage(),
          error: (err) => {
            const errorMessage =
              typeof err === 'string'
                ? err.split(':')[0]
                : err?.detail?.split(':')[0] || 'Error al enviar el formulario';
            this.formErrorMessage = errorMessage;
            console.error('Error submitting form:', err);
          },
        });
      }
    } catch (error) {
      console.error('Error submitting form:', error);
    }
  }

  getEditOrCreateCustomer(customer: Customer) {
    return this.editForm()
      ? this.customerService.updateCustomer(customer.id!, customer)
      : this.customerService.createCustomer(customer);
  }

  generateAccountNumber() {
    const randomNumber = Math.floor(1000000000 + Math.random() * 9000000000);
    this.accountNumber.setValue(randomNumber.toString());
  }

  getCreateWithAccount(customerWithAccount: CustomerWithAccount) {
    console.log('Creating customer with account:', customerWithAccount);
    return this.customerService.createCustomerWithAccount(customerWithAccount);
  }

  resetForm() {
    this.navigateListPage();
  }

  navigateListPage() {
    const route = this.editForm() ? ['../..'] : ['..'];
    this.router.navigate([...route, 'list'], {
      relativeTo: this.activatedRoute,
    });
  }

  //#region Customer Form Getters
  get fullName(): FormControl {
    return this.customerForm.get('fullName') as FormControl;
  }

  get gender(): FormControl {
    return this.customerForm.get('gender') as FormControl;
  }

  get age(): FormControl {
    return this.customerForm.get('age') as FormControl;
  }

  get identification(): FormControl {
    return this.customerForm.get('identification') as FormControl;
  }

  get address(): FormControl {
    return this.customerForm.get('address') as FormControl;
  }

  get celular(): FormControl {
    return this.customerForm.get('celular') as FormControl;
  }

  get email(): FormControl {
    return this.customerForm.get('email') as FormControl;
  }

  get password(): FormControl {
    return this.customerForm.get('password') as FormControl;
  }

  get active(): FormControl {
    return this.customerForm.get('active') as FormControl;
  }
  //#endregion

  //#region Account Form Getters
  get accountNumber(): FormControl {
    return this.accountForm.get('accountNumber') as FormControl;
  }

  get accountType(): FormControl {
    return this.accountForm.get('accountType') as FormControl;
  }

  get initialBalance(): FormControl {
    return this.accountForm.get('initialBalance') as FormControl;
  }

  get accountStatus(): FormControl {
    return this.accountForm.get('status') as FormControl;
  }
  //#endregion
}
