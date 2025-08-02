import { Component, inject, signal } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import {
  PichinchaButtonModule,
  PichinchaInputModule,
  PichinchaReactiveControlsModule,
  PichinchaTypographyModule,
} from '@pichincha/ds-angular';

import { Product } from '../../interfaces/product.interface';
import { CrudService } from '../../services/crud.service';

@Component({
  selector: 'app-edit-item',
  standalone: true,
  imports: [
    PichinchaTypographyModule,
    PichinchaInputModule,
    PichinchaButtonModule,
    PichinchaReactiveControlsModule,
    /* ---------- */
    RouterModule,
    ReactiveFormsModule,
  ],
  providers: [],
  styleUrls: ['./edit-item.component.scss'],
  templateUrl: './edit-item.component.html',
})
export class EditItemComponent {
  crudService = inject(CrudService);
  router = inject(Router);
  fb = inject(FormBuilder);
  activatedRoute = inject(ActivatedRoute);

  editForm = signal<boolean>(false);

  registerForm!: FormGroup;

  constructor() {
    this.buildForm();

    const { product } = this.activatedRoute.snapshot.data;
    this.setValueForm(product);
  }

  buildForm() {
    this.registerForm = this.fb.group({
      id: [0],
      title: [null, Validators.required],
      description: [null, Validators.required],
      price: [null, Validators.required],
      discountPercentage: [null, Validators.required],
      stock: [null, Validators.required],
    });
  }

  setValueForm(itemProduct?: Product) {
    if (!itemProduct) return;

    this.editForm.set(true);
    this.registerForm.patchValue(itemProduct);
  }

  async onSubmit() {
    try {
      this.registerForm.markAllAsTouched();

      if (this.registerForm.invalid) throw new Error('');

      const productDto = this.registerForm.getRawValue();

      await this.getEditOrCreate(productDto);
      this.navigateListPage();
    } catch (error) {}
  }

  getEditOrCreate({ id, ...restProduct }: Product) {
    return this.editForm()
      ? this.crudService.update(id, restProduct)
      : this.crudService.create(restProduct);
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

  //#region Getters
  get title(): FormControl {
    return this.registerForm.get('title') as FormControl;
  }

  get description(): FormControl {
    return this.registerForm.get('description') as FormControl;
  }

  get price(): FormControl {
    return this.registerForm.get('price') as FormControl;
  }

  get discountPercentage(): FormControl {
    return this.registerForm.get('discountPercentage') as FormControl;
  }

  get stock(): FormControl {
    return this.registerForm.get('stock') as FormControl;
  }

  //#endregion
}
