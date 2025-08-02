import { inject, Injectable } from '@angular/core';
import { HttpService } from '@pichincha/angular-sdk/http';
import { Product, ResponseProduct } from '../interfaces/product.interface';
import { CreateDtoProducto } from '../types/product.type';

@Injectable({
  providedIn: 'root',
})
export class CrudService {
  private http: HttpService = inject(HttpService);

  getAll(): Promise<ResponseProduct> {
    // console.log("this.http -->", this.http)
    return this.http.get('products');
  }

  getById(idProduct: number): Promise<Product> {
    return this.http.get('products/' + idProduct);
  }

  create(producto: CreateDtoProducto): Promise<boolean> {
    return this.http.post('products/add', producto);
  }

  update(idProduct: number, producto: CreateDtoProducto): Promise<boolean> {
    return this.http.put('products/' + idProduct, producto);
  }

  delete(idProduct: number): Promise<boolean> {
    return this.http.delete('products/' + idProduct);
  }
}
