export interface MovementRequest {
    accountNumber: string;
    type: 'CREDIT' | 'DEBIT';
    value: number;
    balance: number;
}