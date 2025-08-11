import { Customer } from "./customer.interface";
export interface AccountRequest {
    accountNumber: string;
    accountType: 'SAVINGS' | 'CURRENT';
    initialBalance: number;
    status: boolean;
    customerId: string;
    customer: Customer;
}