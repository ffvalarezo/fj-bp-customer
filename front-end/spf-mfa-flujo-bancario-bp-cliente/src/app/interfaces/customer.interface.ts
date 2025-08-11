export interface Customer {
  id?: number;
  fullName: string;
  gender: string;
  age: number;
  identification: string;
  address: string;
  celular: string;
  email: string;
  customerId?: number;
  password: string;
  active: boolean;
}

export interface AccountRequest {
  accountNumber: string;
  accountType: 'SAVINGS' | 'CURRENT';
  initialBalance: number;
  status: boolean;
  customerId: number;
}

export interface CustomerWithAccount {
  customer: Customer;
  account: AccountRequest;
}
