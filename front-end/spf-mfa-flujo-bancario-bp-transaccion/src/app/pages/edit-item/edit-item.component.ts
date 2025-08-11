import { Component, inject, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';
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
  PichinchaDropdownModule,
  PichinchaSelectorListModalModule,
  PichinchaInputMessageModule,
  PichinchaDetailItemListModule,
} from '@pichincha/ds-angular';
import { MovementService } from 'src/app/services/movement.service';
import { MovementResponse } from 'src/app/interfaces/movement-response.interface';
import { MovementRequest } from 'src/app/interfaces/movement-request.interface';
import { Customer } from 'src/app/interfaces/customer.interface';
import { AccountRequest } from 'src/app/interfaces/account-request.interface';
import { CustomerService } from 'src/app/services/customer.service';
import { AccountService } from 'src/app/services/account.service';

@Component({
  selector: 'app-edit-item',
  standalone: true,
  imports: [
    CommonModule,
    PichinchaTypographyModule,
    PichinchaInputModule,
    PichinchaButtonModule,
    PichinchaReactiveControlsModule,
    PichinchaDropdownModule,
    PichinchaSelectorListModalModule,
    PichinchaInputMessageModule,
    PichinchaDetailItemListModule,
    /* ---------- */
    RouterModule,
    ReactiveFormsModule,
  ],
  providers: [],
  styleUrls: ['./edit-item.component.scss'],
  templateUrl: './edit-item.component.html',
})
export class EditItemComponent {
  movementService = inject(MovementService);
  accountService = inject(AccountService);
  customerService = inject(CustomerService);
  router = inject(Router);
  fb = inject(FormBuilder);
  activatedRoute = inject(ActivatedRoute);

  editForm = signal<boolean>(false);

  registerForm!: FormGroup;
  accountResponse = signal<AccountRequest | undefined>(undefined);
  customerResponse = signal<Customer | undefined>(undefined);
  customerId = signal<string | undefined>(undefined);
  accountId = signal<string | undefined>(undefined);

  // Propiedades del dropdown
  state = 'normal';
  size = 'medium';
  placeholder = 'Selecciona una cuenta';
  label = 'Cuenta destino';
  tooltip = 'Selecciona la cuenta destino';
  formErrorMessage: string = '';

  items: {
    label: string;
    value: string;
  }[] = [];

  constructor() {
    this.buildForm();

    const { movement } = this.activatedRoute.snapshot.data;
    this.setValueForm(movement);

    // Cargar datos para el dropdown
    this.getData();
  }

  getData() {
    this.customerId.set(sessionStorage.getItem('customerId') ?? undefined);
    this.accountId.set(sessionStorage.getItem('accountId') ?? undefined);
    const customerId = Number(this.customerId());
    const account = this.accountId();
    if (!customerId || !account) {
      console.error('Invalid customer ID');
    }
    if (typeof account === 'string') {
      this.accountService.getAccountsByAccountNumber(account).subscribe({
        next: (accounts) => {
          this.accountResponse.set(accounts[0]);
        },
        error: (error) => {
          console.error('Error loading accounts:', error);
        },
      });
    }

    this.customerService.getCustomerById(customerId).subscribe({
      next: (customer) => {
        this.customerResponse.set(customer);
      },
      error: (error) => {
        console.error('Error loading customers:', error);
      },
    });

    this.accountService.getAllAccounts().subscribe({
      next: async (accounts) => {
        const list = await Promise.all(
          accounts
            .filter(account => account.accountNumber !== this.accountId())
            .map(async account => {
              const customer = await firstValueFrom(this.customerService.getCustomerById(account.customerId));
              return {
                label: `${account.accountNumber} - ${customer?.fullName ?? ''}`,
                value: account.accountNumber
              };
            })
        );
        this.items = list;
      },
      error: (error) => {
        console.error('Error loading all accounts:', error);
      },
    });

  }

  buildForm() {
    this.registerForm = this.fb.group({
      accountNumber: [null, Validators.required],
      value: [null, Validators.required],
    });
  }

  setValueForm(itemMovement?: MovementResponse) {
    if (!itemMovement) return;

    this.editForm.set(true);
    this.registerForm.patchValue(itemMovement);
  }

  onSubmit() {
    try {
      console.log('Form submitted:', this.registerForm.getRawValue());
      this.registerForm.markAllAsTouched();

      if (this.registerForm.invalid) throw new Error('Formulario inválido');

      const movementDto = this.registerForm.getRawValue();
      const initialBalance = this.accountResponse()?.initialBalance ?? 0;
      const value = Number(movementDto.value);
      if (value > initialBalance) {
        this.formErrorMessage = 'El valor no puede ser mayor al saldo de la cuenta';
        return;
      }
      this.generateDebit(
        initialBalance,
        value,
        () => {
          this.generateCredit(
            value,
            movementDto.accountNumber, () => { },
            () => {
              this.formErrorMessage = 'Error al realizar el crédito. Por favor, verifica los datos e intenta nuevamente.';
            }
          );
        },
        () => {
          this.formErrorMessage = 'Error al realizar el débito. Por favor, verifica los datos e intenta nuevamente.';
        }
      );
    } catch (error) { console.error(error); }
  }
  generateCredit(
    value: number,
    accountNumber: string,
    onSuccess: () => void,
    onError?: () => void
  ): void {
    let accountUpdate: AccountRequest | undefined;

    if (accountNumber) {
      this.accountService.getAccountsByAccountNumber(accountNumber).subscribe({
        next: (accounts) => {
          accountUpdate = accounts[0];
          if (accountUpdate) {
            const movement: MovementRequest = {
              accountNumber,
              type: 'CREDIT',
              value,
              balance: accountUpdate.initialBalance + value,
            };
            this.createMovementAndUpdateAccount(
              movement,
              accountUpdate,
              () => {
                if (onSuccess) onSuccess();
                this.navigateListPage();
              },
              () => {
                if (onError) onError();
              }
            );
          } else {
            console.error('Account update information is undefined');
            this.formErrorMessage = 'Error: La informacion de la cuenta es indefinida';
            if (onError) onError();
          }
        },
        error: (error) => {
          console.error('Error loading account:', error);
          this.formErrorMessage = 'Error al obtener la cuenta para crédito';
          if (onError) onError();
        }
      });
    } else {
      this.formErrorMessage = 'Error: El número de cuenta es indefinido';
      if (onError) onError();
    }
  }
  generateDebit(initialBalance: number, value: number, onSuccess: () => void, onError?: () => void): void {
    const accountNumber = this.accountId();
    const accountUpdate = this.accountResponse();
    if (!accountNumber) {
      console.error('Account number is undefined');
      if (onError) onError();
      return;
    }

    const movement: MovementRequest = {
      accountNumber,
      type: 'DEBIT',
      value,
      balance: initialBalance - value,
    };

    if (accountUpdate) {
      this.createMovementAndUpdateAccount(movement, accountUpdate, onSuccess, onError);
    } else {
      console.error('Account update information is undefined');
      this.formErrorMessage = 'Error: La informacion de la cuenta es indefinida';
      if (onError) onError();
    }
  }

  private createMovementAndUpdateAccount(
    movement: MovementRequest,
    accountUpdate: AccountRequest,
    onSuccess: () => void,
    onError?: () => void
  ): void {
    this.movementService.createMovement(movement).subscribe({
      next: () => {
        this.updateAccountBalance(movement.accountNumber, movement.balance, accountUpdate, onSuccess, onError);
      },
      error: (error) => {
        if (error?.error?.errors?.length > 0) {
          const err = error.error.errors[0];
          this.formErrorMessage = `${err.message ? err.message + ': ' : ''}${err.businessMessage}`;
        } else if (error?.error?.detail) {
          this.formErrorMessage = error.error.detail;
        } else {
          this.formErrorMessage = 'Error al transferir';
        }
        console.error(error);
        if (onError) onError();
      }
    });
  }

  private updateAccountBalance(
    accountNumber: string,
    newBalance: number,
    accountUpdate: AccountRequest,
    onSuccess: () => void,
    onError?: () => void
  ): void {
    const account = accountUpdate;
    if (!account) {
      console.error('Account response is undefined');
      if (onError) onError();
      return;
    }

    const updatedAccount: AccountRequest = {
      ...account,
      accountNumber,
      initialBalance: newBalance,
    };

    if (updatedAccount.id !== undefined) {
      this.accountService.updateAccount(updatedAccount.id, updatedAccount).subscribe({
        next: () => {
          this.accountResponse.set(updatedAccount);
          if (onSuccess) onSuccess();
        },
        error: (error) => {
          this.formErrorMessage = 'Error al actualizar la cuenta';
          console.error(error);
          if (onError) onError();
        }
      });
    } else {
      console.error('Account id is undefined');
      this.formErrorMessage = 'Error: Account id is undefined';
      if (onError) onError();
    }
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

  get id(): FormControl {
    return this.registerForm.get('id') as FormControl;
  }

  get accountNumber(): FormControl {
    return this.registerForm.get('accountNumber') as FormControl;
  }

  get type(): FormControl {
    return this.registerForm.get('type') as FormControl;
  }

  get value(): FormControl {
    return this.registerForm.get('value') as FormControl;
  }

  get balance(): FormControl {
    return this.registerForm.get('balance') as FormControl;
  }

  get date(): FormControl {
    return this.registerForm.get('date') as FormControl;
  }

  handleClickedItem(event: Event) {
    const { detail } = event as unknown as CustomEvent;
    if (detail && detail[0].value) {
      this.registerForm.patchValue({
        accountNumber: detail[0].value
      });
    }
  }

}