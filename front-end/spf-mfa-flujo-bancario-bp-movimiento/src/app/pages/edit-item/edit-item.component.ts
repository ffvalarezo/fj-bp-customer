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

import { AccountRequest } from '../../interfaces/account-request.interface';
import { AccountService } from '../../services/account.service';

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
  accountService = inject(AccountService);
  router = inject(Router);
  fb = inject(FormBuilder);
  activatedRoute = inject(ActivatedRoute);
  editForm = signal<boolean>(false);
  formErrorMessage: string = '';

  registerForm!: FormGroup;

  constructor() {
    this.buildForm();

    const { account } = this.activatedRoute.snapshot.data;
    this.setValueForm(account);
  }

  buildForm() {
    this.registerForm = this.fb.group({
      id: [0],
      accountNumber: [null, Validators.required],
      accountType: [null, Validators.required],
      initialBalance: [null, Validators.required],
      status: [null, Validators.required],
    });
  }

  setValueForm(itemAccount?: AccountRequest) {
    if (!itemAccount) return;

    this.editForm.set(true);
    this.registerForm.patchValue(itemAccount);
  }

  onSubmit() {
    try {
      this.registerForm.markAllAsTouched();

      if (this.registerForm.invalid) throw new Error('');

      const accountDto = this.registerForm.getRawValue();

      const customerId = sessionStorage.getItem('customerId');
      if (customerId) {
        accountDto.customerId = customerId;
      }

      this.getEditOrCreate(accountDto).subscribe({
        next: () => this.navigateListPage(),
        error: (err) => {
          this.formErrorMessage =
            typeof err === 'string'
              ? err.split(':')[0]
              : err?.detail?.split(':')[0] || 'Error al enviar el formulario';
          console.error('Error submitting form:', err);
        },
      });
    } catch (error) {
      console.error('Error submitting form:', error);
    }
  }

  getEditOrCreate({ id, ...restAccount }: AccountRequest) {
    return this.editForm()
      ? this.accountService.updateAccount(id!, restAccount)
      : this.accountService.createAccount(restAccount);
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
  generateAccountNumber() {
    const randomNumber = Math.floor(1000000000 + Math.random() * 9000000000);
    this.accountNumber.setValue(randomNumber.toString());
  }

  //#region Getters
  get accountNumber(): FormControl {
    return this.registerForm.get('accountNumber') as FormControl;
  }

  get accountType(): FormControl {
    return this.registerForm.get('accountType') as FormControl;
  }

  get initialBalance(): FormControl {
    return this.registerForm.get('initialBalance') as FormControl;
  }

  get status(): FormControl {
    return this.registerForm.get('status') as FormControl;
  }

  //#endregion
}
