import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import {
  PichinchaButtonModule,
  PichinchaLoadingScreenModule,
  PichinchaTypographyModule,
  PichinchaTableModule,
} from '@pichincha/ds-angular';

import { AccountService } from '../../services/account.service';
import { CustomerService } from 'src/app/services/customer.service';

import { Actions, Header, Row } from '@pichincha/ds-core';
import { AccountRequest } from '../../interfaces/account-request.interface';
import { Customer } from 'src/app/interfaces/customer.interface';

@Component({
  selector: 'app-list-items',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    PichinchaTypographyModule,
    PichinchaButtonModule,
    PichinchaLoadingScreenModule,
    PichinchaTableModule,
  ],
  providers: [AccountService, CustomerService],
  templateUrl: './list-items.component.html',
  styleUrl: './list-items.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListItemsComponent implements OnInit {
  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);
  private accountService = inject(AccountService);
  private customerService = inject(CustomerService);

  loadingData = signal<boolean>(true);

  openModal = signal<boolean>(false);
  idItem = signal<string | undefined>(undefined);
  message = signal<string | undefined>(undefined);

  fullNameCustomer = signal<string | undefined>(undefined);

  itemss = signal<Row[]>([]);
  currentPage: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  totalPages: number = 0;

  tableHeaders: Header[] = [];
  tableActions: Actions[] = [];
  Math = Math;

  ngOnInit() {
    this.configTable();
    this.getAccounts();
  }

  getAccounts() {
    this.loadingData.set(true);

    this.message.set(sessionStorage.getItem('customerId') ?? undefined);

    const customerId = Number(this.message());
    if (!customerId) {
      console.error('Invalid customer ID');
      this.itemss.set([]);
      this.currentPage = 0;
      this.totalElements = 0;
      this.totalPages = 0;
      return;
    }

    this.accountService.getAccountsByCustomerId(customerId).subscribe({
      next: (accounts) => {
        this.customerService.getCustomerById(customerId).subscribe({
          next: (customer) => {
            const rows = accounts.map((account: AccountRequest) =>
              this.mapCustomerRowData(account)
            );
            this.fullNameCustomer.set(customer.fullName);
            this.itemss.set(rows);
            this.currentPage = 1;
            this.totalElements = rows.length;
            this.totalPages = Math.ceil(rows.length / this.pageSize);
          },
          error: (error) => {
            console.error('Error loading customer:', error);
            this.itemss.set([]);
            this.currentPage = 0;
            this.totalElements = 0;
            this.totalPages = 0;
          },
        });
      },
      error: (error) => {
        console.error('Error loading accounts:', error);
        this.itemss.set([]);
        this.currentPage = 0;
        this.totalElements = 0;
        this.totalPages = 0;
      },
    });

    this.loadingData.set(false);
  }

  configTable() {
    this.tableHeaders = [
      { id: 'accountNumber', label: 'Número', width: '150px' },
      { id: 'accountType', label: 'Tipo', width: '100px' },
      { id: 'initialBalance', label: 'Balance', width: '100px' },
    ];

    this.tableActions = [
      {
        id: 'transferir',
        label: 'Movimientos',
        action: (row: Row) => {
          this.idItem.set(row.id);
          this.handleClickTransfer();
        },
      },
    ];
  }

  handleCreateANewItem() {
    this.router.navigate(['..', 'edit'], { relativeTo: this.activatedRoute });
  }
  handleClickTransfer() {
    sessionStorage.setItem('accountId', this.idItem() ?? '');
    this.router.navigate(['/transaccion']);
  }
  handleGenerateReport() {
    this.router.navigate(['/transaccion/report']);
  }

  handleReturnCustomer() {
    sessionStorage.removeItem('customerId');
    sessionStorage.removeItem('accountId');
    this.router.navigate(['/cliente']);
  }

  mapCustomerRowData = (account: AccountRequest): Row => {
    let accountTypeLabel: string;
    if (account.accountType === 'SAVINGS') {
      accountTypeLabel = 'Ahorros';
    } else if (account.accountType === 'CURRENT') {
      accountTypeLabel = 'Corriente';
    } else {
      accountTypeLabel = account.accountType;
    }

    return {
      id: account.accountNumber ?? '',
      label: '',
      columns: [
        {
          headerId: 'accountNumber',
          primaryText: account.accountNumber,

        },
        {
          headerId: 'accountType',
          primaryText: accountTypeLabel,
        },
        {
          headerId: 'initialBalance',
          primaryText: account.initialBalance.toString(),
        },
      ],
    };
  };
}
