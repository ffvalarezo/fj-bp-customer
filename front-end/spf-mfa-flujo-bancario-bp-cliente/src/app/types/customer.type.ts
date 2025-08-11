import { Customer } from '../interfaces/customer.interface';

export type CreateDtoCustomer = Partial<Omit<Customer, 'id'>>;
