import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditItemComponent } from './edit-item.component';
import { Router, RouterModule } from '@angular/router';
import { CrudService } from '../../services/crud.service';
import { Product } from 'src/app/interfaces/product.interface';

describe('EditItemComponent', () => {
  let fixture: ComponentFixture<EditItemComponent>;
  let compiled: HTMLElement;
  let component: EditItemComponent;

  let router: Router;

  let mockCrudService = {
    update: jest.fn(),
    create: jest.fn(),
  };

  const productMock: Product = {
    id: 1,
    title: 'title',
    description: 'description',
    price: 1,
    discountPercentage: 1,
    rating: 1,
    stock: 1,
    brand: 'brand',
    category: 'category',
    thumbnail: 'thumbnail',
    images: ['url'],
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        EditItemComponent,
        RouterModule.forRoot([{ path: '', component: EditItemComponent }]),
      ],
      providers: [{ provide: CrudService, useValue: mockCrudService }],
    }).compileComponents();

    router = TestBed.inject(Router);

    fixture = TestBed.createComponent(EditItemComponent);
    compiled = fixture.nativeElement as HTMLElement;
    component = fixture.componentInstance;
  });

  afterEach(() => {
    mockCrudService.create.mockRestore();
    mockCrudService.update.mockRestore();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should execute setValueForm with a product', () => {
    component.setValueForm(productMock);

    expect(component.registerForm.getRawValue()).toStrictEqual({
      description: 'description',
      discountPercentage: 1,
      id: 1,
      price: 1,
      stock: 1,
      title: 'title',
    });
  });

  it('should create product', async () => {
    const navigateMock = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    component.registerForm.patchValue(productMock);

    await component.onSubmit();

    expect(mockCrudService.create).toBeCalled();
    expect(navigateMock).toBeCalled();
  });

  it('should not create product beacause the form is invalid', async () => {
    await component.onSubmit();

    expect(mockCrudService.create).toBeCalledTimes(0);
  });

  it('should update product', async () => {
    const navigateMock = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.registerForm.patchValue(productMock);
    component.editForm.set(true);

    await component.onSubmit();

    expect(mockCrudService.update).toBeCalledTimes(1);
    expect(mockCrudService.create).toBeCalledTimes(0);
    expect(navigateMock).toBeCalledTimes(1);
  });

  it('should execute resetForm', () => {
    const navigateMock = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.resetForm();

    expect(navigateMock).toBeCalledTimes(1);
  });

  it('should get title control', () => {
    expect(component.title).toBe(component.registerForm.get('title'));
  });

  it('should get description control', () => {
    expect(component.description).toBe(
      component.registerForm.get('description')
    );
  });

  it('should get price control', () => {
    expect(component.price).toBe(component.registerForm.get('price'));
  });

  it('should get discountPercentage control', () => {
    expect(component.discountPercentage).toBe(
      component.registerForm.get('discountPercentage')
    );
  });

  it('should get stock control', () => {
    expect(component.stock).toBe(component.registerForm.get('stock'));
  });
});
