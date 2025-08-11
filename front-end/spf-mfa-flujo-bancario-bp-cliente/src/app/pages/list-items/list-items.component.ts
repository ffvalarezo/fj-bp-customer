import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import {
  PichinchaButtonModule,
  PichinchaLoadingScreenModule,
  PichinchaTypographyModule,
  PichinchaTableModule,
} from '@pichincha/ds-angular';

import { ModalListItemComponent } from '../../components/modal-list-item/modal-list-item.component';

import { CustomerService } from '../../services/customer.service';

import { Actions, Header, Row } from '@pichincha/ds-core';
import { Customer } from '../../interfaces/customer.interface';

@Component({
  selector: 'app-list-items',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    PichinchaTypographyModule,
    PichinchaButtonModule,
    PichinchaLoadingScreenModule,
    ModalListItemComponent,
    PichinchaTableModule,
  ],
  providers: [CustomerService],
  templateUrl: './list-items.component.html',
  styleUrl: './list-items.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListItemsComponent implements OnInit {
  private router = inject(Router);
  private activatedRoute = inject(ActivatedRoute);
  private customerService = inject(CustomerService);

  loadingData = signal<boolean>(true);

  openModal = signal<boolean>(false);
  idItem = signal<string | undefined>(undefined);

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
    this.getCustomers();
  }

  getCustomers() {
    this.loadingData.set(true);
    sessionStorage.removeItem('customerId');
    sessionStorage.removeItem('accountId');
    this.customerService.getAllCustomers().subscribe({
      next: (resp) => {
        this.itemss.set(
          resp.map((customer) => this.mapCustomerRowData(customer))
        );
        this.currentPage = 1;
        this.totalElements = resp.length;
        this.totalPages = Math.ceil(resp.length / this.pageSize);
      },
      error: (error) => {
        console.error('Error loading customers:', error);
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
      { id: 'identification', label: 'Cédula', width: '150px' },
      { id: 'fullName', label: 'Nombre', width: '200px' },
      { id: 'gender', label: 'Género', width: '100px' },
      { id: 'age', label: 'Edad', width: '80px' },
      { id: 'celular', label: 'Celular', width: '150px' },
    ];

    this.tableActions = [
      {
        id: 'editar',
        label: 'Editar',
        action: (row: Row) => {
          this.idItem.set(row.id);
          this.handleClickEdit();
        },
      },
      {
        id: 'eliminar',
        label: 'Eliminar',
        action: (row: Row) => {
          this.idItem.set(row.id);
          this.openModal.set(true);
        },
      },
      {
        id: 'cuenta',
        label: 'Cuentas',
        action: (row: Row) => {
          this.idItem.set(row.id);
          this.handleAccount();
        },
      },
    ];
  }

  handleAccount() {
    sessionStorage.setItem('customerId', this.idItem() ?? '');
    this.router.navigate(['/movimiento']);
  }

  handleCreateANewItem() {
    this.router.navigate(['..', 'edit'], { relativeTo: this.activatedRoute });
  }

  handleClickEdit() {
    this.router.navigate(['..', 'edit', this.idItem()], {
      relativeTo: this.activatedRoute,
    });
  }

  handleClickDelete() {
    this.customerService
      .deleteCustomer(Number(this.idItem()))
      .subscribe((isOkDelete) => {
        if (isOkDelete) {
          this.idItem.set(undefined);
          this.openModal.set(false);
          this.getCustomers();
        }
      });
  }

  handleCloseModal() {
    this.idItem.set(undefined);
    this.openModal.set(false);
  }

  mapCustomerRowData = (customer: Customer): Row => {
    return {
      id:
        customer.customerId !== undefined ? customer.customerId.toString() : '',
      label: '',
      columns: [
        {
          headerId: 'identification',
          primaryText: customer.identification,
        },
        {
          headerId: 'fullName',
          primaryText: customer.fullName,
        },
        {
          headerId: 'gender',
          primaryText: customer.gender,
        },
        {
          headerId: 'age',
          primaryText: customer.age.toString(),
        },
        {
          headerId: 'celular',
          primaryText: customer.celular,
        },
      ],
    };
  };
}
