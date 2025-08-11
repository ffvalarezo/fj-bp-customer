export interface AccountRequest {
    id?: number;
    accountNumber: string;
    accountType: 'SAVINGS' | 'CURRENT';
    initialBalance: number;
    status: boolean;
    customerId: number;
}