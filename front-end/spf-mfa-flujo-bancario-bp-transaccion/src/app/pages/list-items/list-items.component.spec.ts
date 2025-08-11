import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ListItemsComponent } from './list-items.component';
import { CrudService } from '../../services/crud.service';
import { RouterModule } from '@angular/router';
import { HttpService } from '@pichincha/angular-sdk/http';

const MockHttpService = {
  get: jest.fn(() => Promise.resolve({ products: [{}] })),
};

class MockCrudService {
  getAll = jest.fn(() => Promise.resolve({ products: [{}] }));
}

describe('BaseComponent', () => {
  let fixture: ComponentFixture<ListItemsComponent>;
  let compiled: HTMLElement;
  let component: ListItemsComponent;

  let mockCrudService: MockCrudService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListItemsComponent, RouterModule.forRoot([])],
      providers: [
        {
          provide: CrudService,
          useValue: MockCrudService,
        },
        {
          provide: HttpService,
          useValue: MockHttpService,
        },
      ],
    }).compileComponents();

    mockCrudService = TestBed.inject(CrudService) as unknown as MockCrudService;

    fixture = TestBed.createComponent(ListItemsComponent);
    compiled = fixture.nativeElement as HTMLElement;
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should call getProducts on init', () => {
    const spyGetProducts = jest.spyOn(component, 'getProducts');
    component.ngOnInit();
    expect(spyGetProducts).toHaveBeenCalled();
  });

  it('should navigate to edit page when handleCreateANewItem is called', () => {
    const routerSpy = jest.spyOn(component['router'], 'navigate');
    component.handleCreateANewItem();
    expect(routerSpy).toHaveBeenCalledWith(['..', 'edit'], {
      relativeTo: component['activatedRoute'],
    });
  });

  it('should navigate to edit page when handleClickEdit is called', () => {
    const routerSpy = jest.spyOn(component['router'], 'navigate');
    const mockIdItem = '123';

    component.idItem.set(mockIdItem);
    component.handleClickEdit();

    expect(routerSpy).toHaveBeenCalledWith(['..', 'edit', mockIdItem], {
      relativeTo: component['activatedRoute'],
    });
  });

  it('should delete item and refresh the list when handleClickDelete is called', async () => {
    const mockIdItem = '123';
    const mockIsOkDelete = true;

    jest.spyOn(component['router'], 'navigate');
    jest
      .spyOn(component['crudService'], 'delete')
      .mockResolvedValue(mockIsOkDelete);
    jest.spyOn(component, 'getProducts');

    component.idItem.set(mockIdItem);
    component.openModal.set(true);

    await component.handleClickDelete();

    expect(component['crudService'].delete).toHaveBeenCalledWith(
      Number(mockIdItem)
    );
    expect(component.idItem()).toBeUndefined();
    expect(component.openModal()).toBe(false);
    expect(component.getProducts).toHaveBeenCalled();
  });

  it('should set idItem and openModal when handleClickItem is called', () => {
    const mockDetail = '123';
    component.handleClickItem({ detail: mockDetail } as unknown as CustomEvent);
    expect(component.idItem()).toBe(mockDetail);
    expect(component.openModal()).toBe(true);
  });
  
  it('should reset idItem and close the modal when handleCloseModal is called', () => {
    component.idItem.set('123');
    component.openModal.set(true);

    component.handleCloseModal();

    expect(component.idItem()).toBeUndefined();
    expect(component.openModal()).toBe(false);
  });
});
