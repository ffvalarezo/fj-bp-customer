import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListItemsComponent } from './list-items.component';
import { Router, ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { CustomerService } from '../../services/customer.service';
import { Row, Header, Actions } from '@pichincha/ds-core';

class MockRouter {
  navigate = jest.fn();
}
class MockActivatedRoute {}

const mockCustomers = [
  {
    customerId: 1,
    identification: '1234567890',
    fullName: 'John Doe',
    gender: 'M',
    age: 30,
    celular: '0999999999',
  },
  {
    customerId: 2,
    identification: '0987654321',
    fullName: 'Jane Doe',
    gender: 'F',
    age: 25,
    celular: '0888888888',
  },
];

class MockCustomerService {
  getAllCustomers = jest.fn(() => of(mockCustomers));
  deleteCustomer = jest.fn(() => of(true));
}

describe('ListItemsComponent', () => {
  let fixture: ComponentFixture<ListItemsComponent>;
  let component: ListItemsComponent;
  let customerService: MockCustomerService;
  let router: MockRouter;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListItemsComponent],
      providers: [
        { provide: CustomerService, useClass: MockCustomerService },
        { provide: Router, useClass: MockRouter },
        { provide: ActivatedRoute, useClass: MockActivatedRoute },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ListItemsComponent);
    component = fixture.componentInstance;
    customerService = TestBed.inject(
      CustomerService
    ) as unknown as MockCustomerService;
    router = TestBed.inject(Router) as unknown as MockRouter;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call configTable and getProducts on ngOnInit', () => {
    const configSpy = jest.spyOn(component, 'configTable');
    const getProductsSpy = jest.spyOn(component, 'getProducts');
    component.ngOnInit();
    expect(configSpy).toHaveBeenCalled();
    expect(getProductsSpy).toHaveBeenCalled();
  });

  it('should set tableHeaders and tableActions in configTable', () => {
    component.configTable();
    expect(component.tableHeaders.length).toBe(5);
    expect(component.tableHeaders[0].id).toBe('identification');
    expect(component.tableHeaders[1].id).toBe('fullName');
    expect(component.tableHeaders[2].id).toBe('gender');
    expect(component.tableHeaders[3].id).toBe('age');
    expect(component.tableHeaders[4].id).toBe('celular');
    expect(component.tableActions.length).toBe(2);
    expect(component.tableActions[0].id).toBe('editar');
    expect(component.tableActions[1].id).toBe('eliminar');
  });

  it('should get products and set itemss, currentPage, totalElements, totalPages', () => {
    component.getProducts();
    expect(customerService.getAllCustomers).toHaveBeenCalled();
    expect(component.itemss().length).toBe(2);
    expect(component.currentPage).toBe(1);
    expect(component.totalElements).toBe(2);
    expect(component.totalPages).toBe(1);
  });

  it('should handle error in getProducts', () => {
    customerService.getAllCustomers = jest.fn(() =>
      throwError(() => new Error('test error'))
    );
    const consoleSpy = jest.spyOn(console, 'error').mockImplementation();
    component.getProducts();
    expect(component.itemss().length).toBe(0);
    expect(component.currentPage).toBe(0);
    expect(component.totalElements).toBe(0);
    expect(component.totalPages).toBe(0);
    expect(consoleSpy).toHaveBeenCalledWith(
      'Error loading customers:',
      expect.any(Error)
    );
    consoleSpy.mockRestore();
  });

  it('should navigate to edit page when handleCreateANewItem is called', () => {
    component.handleCreateANewItem();
    expect(router.navigate).toHaveBeenCalledWith(['..', 'edit'], {
      relativeTo: expect.anything(),
    });
  });

  it('should navigate to edit page with id when handleClickEdit is called', () => {
    component.idItem.set('1');
    component.handleClickEdit();
    expect(router.navigate).toHaveBeenCalledWith(['..', 'edit', '1'], {
      relativeTo: expect.anything(),
    });
  });

  it('should delete customer and refresh list on handleClickDelete', () => {
    const getProductsSpy = jest.spyOn(component, 'getProducts');
    component.idItem.set('1');
    component.openModal.set(true);
    component.handleClickDelete();
    expect(customerService.deleteCustomer).toHaveBeenCalledWith(1);
    expect(component.idItem()).toBeUndefined();
    expect(component.openModal()).toBe(false);
    expect(getProductsSpy).toHaveBeenCalled();
  });

  it('should not refresh list if deleteCustomer returns false', () => {
    customerService.deleteCustomer = jest.fn(() => of(false));
    const getProductsSpy = jest.spyOn(component, 'getProducts');
    component.idItem.set('1');
    component.openModal.set(true);
    component.handleClickDelete();
    expect(component.idItem()).toBe('1');
    expect(component.openModal()).toBe(true);
    expect(getProductsSpy).not.toHaveBeenCalled();
  });

  it('should handleCloseModal reset idItem and openModal', () => {
    component.idItem.set('1');
    component.openModal.set(true);
    component.handleCloseModal();
    expect(component.idItem()).toBeUndefined();
    expect(component.openModal()).toBe(false);
  });

  it('should map customer to Row correctly', () => {
    const row = component.mapCustomerRowData(mockCustomers[0]);
    expect(row.id).toBe('1');
    expect(row.columns[0].headerId).toBe('identification');
    expect(row.columns[0].primaryText).toBe('1234567890');
    expect(row.columns[1].headerId).toBe('fullName');
    expect(row.columns[1].primaryText).toBe('John Doe');
    expect(row.columns[2].headerId).toBe('gender');
    expect(row.columns[2].primaryText).toBe('M');
    expect(row.columns[3].headerId).toBe('age');
    expect(row.columns[3].primaryText).toBe('30');
    expect(row.columns[4].headerId).toBe('celular');
    expect(row.columns[4].primaryText).toBe('0999999999');
  });

  it('should map customer with undefined customerId', () => {
    const customerWithoutId = {
      customerId: undefined,
      identification: 'id',
      fullName: 'name',
      gender: 'M',
      age: 20,
      celular: '099',
    };
    const row = component.mapCustomerRowData(customerWithoutId);
    expect(row.id).toBe('');
  });

  it('should handle action editar in tableActions', () => {
    component.configTable();
    const editAction = component.tableActions.find((a) => a.id === 'editar');
    const spy = jest.spyOn(component, 'handleClickEdit');
    editAction?.action({ id: '2' } as Row);
    expect(component.idItem()).toBe('2');
    expect(spy).toHaveBeenCalled();
  });

  it('should handle action eliminar in tableActions', () => {
    component.configTable();
    const deleteAction = component.tableActions.find(
      (a) => a.id === 'eliminar'
    );
    deleteAction?.action({ id: '2' } as Row);
    expect(component.idItem()).toBe('2');
    expect(component.openModal()).toBe(true);
  });

  it('should set loadingData to true initially and false after getProducts', () => {
    expect(component.loadingData()).toBe(true);
    component.getProducts();
    expect(component.loadingData()).toBe(false);
  });

  it('should calculate pagination correctly with Math.ceil', () => {
    component.getProducts();
    expect(component.totalPages).toBe(
      Math.ceil(component.totalElements / component.pageSize)
    );
  });

  it('should initialize signals with correct default values', () => {
    expect(component.loadingData()).toBe(true);
    expect(component.openModal()).toBe(false);
    expect(component.idItem()).toBeUndefined();
    expect(component.itemss()).toEqual([]);
  });

  it('should have correct pagination properties initialized', () => {
    expect(component.currentPage).toBe(0);
    expect(component.pageSize).toBe(10);
    expect(component.totalElements).toBe(0);
    expect(component.totalPages).toBe(0);
  });

  it('should have Math property available', () => {
    expect(component.Math).toBe(Math);
  });
});
