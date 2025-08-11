import { inject, Injectable } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  Resolve,
  RouterStateSnapshot,
} from '@angular/router';
import { CustomerService } from '../services/customer.service';
import { Customer } from '../interfaces/customer.interface';
import { firstValueFrom } from 'rxjs';

@Injectable()
export class CustomerResolver implements Resolve<Customer> {
  customerService: CustomerService = inject(CustomerService);

  resolve(
    route: ActivatedRouteSnapshot,
    _state: RouterStateSnapshot
  ): Promise<Customer> {
    const { id } = route.params;
    return firstValueFrom(this.customerService.getCustomerById(id));
  }
}
