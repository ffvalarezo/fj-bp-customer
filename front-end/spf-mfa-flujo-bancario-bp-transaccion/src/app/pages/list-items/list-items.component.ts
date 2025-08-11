import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Header, Row } from '@pichincha/ds-core';
import { CommonModule } from '@angular/common';
import {
  PichinchaButtonModule,
  PichinchaLoadingScreenModule,
  PichinchaTypographyModule,
  PichinchaTableModule,
  PichinchaDetailItemListModule,
} from '@pichincha/ds-angular';
import { AccountService } from '../../services/account.service';
import { MovementService } from '../../services/movement.service';
import { CustomerService } from '../../services/customer.service';
import { MovementResponse } from '../../interfaces/movement-response.interface';
import { AccountRequest } from 'src/app/interfaces/account-request.interface';
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
    PichinchaDetailItemListModule
  ],
  providers: [MovementService, AccountService],
  templateUrl: './list-items.component.html',
  styleUrl: './list-items.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListItemsComponent implements OnInit {
  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);
  private movementService = inject(MovementService);
  private accountService = inject(AccountService);
  private customerService = inject(CustomerService);

  loadingData = signal<boolean>(true);
  customerId = signal<string | undefined>(undefined);
  accountId = signal<string | undefined>(undefined);

  tableHeaders: Header[] = [];
  Math = Math;

  itemss = signal<Row[]>([]);
  currentPage: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  totalPages: number = 0;
  accountResponse = signal<AccountRequest | undefined>(undefined);
  customerResponse = signal<Customer | undefined>(undefined);

  ngOnInit() {
    this.configTable()
    this.getTransaction();
  }

  getTransaction() {
    this.loadingData.set(true);
    this.customerId.set(sessionStorage.getItem('customerId') ?? undefined);
    this.accountId.set(sessionStorage.getItem('accountId') ?? undefined);
    const customerId = Number(this.customerId());
    const account = this.accountId();
    if (!customerId || !account) {
      console.error('Invalid customer ID');
      this.itemss.set([]);
      this.currentPage = 0;
      this.totalElements = 0;
      this.totalPages = 0;
      return;
    }

    this.movementService.getMovementsByAccountNumber(account).subscribe({
      next: (movements) => {
        const rows = movements.map((movement: MovementResponse) =>
          this.mapCustomerRowData(movement)
        );
        this.itemss.set(rows);
        this.currentPage = 1;
        this.totalElements = rows.length;
        this.totalPages = Math.ceil(rows.length / this.pageSize);
      },
      error: (error) => {
        console.error('Error loading movements:', error);
        this.itemss.set([]);
        this.currentPage = 0;
        this.totalElements = 0;
        this.totalPages = 0;
      },
    });
    this.accountService.getAccountsByAccountNumber(account).subscribe({
      next: (accounts) => {
        this.accountResponse.set(accounts[0]);
      },
      error: (error) => {
        console.error('Error loading accounts:', error);
      },
    });

    this.customerService.getCustomerById(customerId).subscribe({
      next: (customer) => {
        this.customerResponse.set(customer);
      },
      error: (error) => {
        console.error('Error loading customers:', error);
      },
    });

    this.loadingData.set(false);
  }

  configTable() {
    this.tableHeaders = [
      { id: 'type', label: 'Tipo', width: '80px' },
      { id: 'value', label: 'Movimiento', width: '80px' },
      { id: 'date', label: 'Fecha', width: '170px' },
    ];
  }
  handleReturnAccount() {
    sessionStorage.removeItem('accountId');
    this.router.navigate(['/movimiento']);
  }
  
  handleCreateANewItem() {
    this.router.navigate(['..', 'edit'], { relativeTo: this.activatedRoute });
  }

  mapCustomerRowData = (movement: MovementResponse): Row => {
    let movementTypeLabel: string;
    if (movement.type === 'DEBIT') {
      movementTypeLabel = 'Retiro';
    } else if (movement.type === 'CREDIT') {
      movementTypeLabel = 'Depósito';
    } else {
      movementTypeLabel = movement.type;
    }

    const date = new Date(Number(movement.date) * 1000);
    const formattedDate = new Date(date).toLocaleString('es-EC', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    });

    return {
      id: movement.id ?? '',
      label: '',
      columns: [
      {
        headerId: 'type',
        primaryText: movementTypeLabel,
      },
      {
        headerId: 'value',
        primaryText: `$${movement.value.toFixed(2)}`,
      },
      {
        headerId: 'date',
        primaryText: formattedDate,
      },
      ],
    };
  };

}
