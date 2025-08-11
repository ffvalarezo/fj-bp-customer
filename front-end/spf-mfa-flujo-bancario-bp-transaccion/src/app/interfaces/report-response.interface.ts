import { Customer } from "./customer.interface";
import { AccountRequest } from "./account-response.interface";
import { MovementResponse } from "./movement-response.interface";

export interface ReportResponseSummary {
    totalCredits: number;
    totalDebits: number;
}

export interface ReportResponse {
    customer: Customer;
    accounts: AccountRequest[];
    movements: MovementResponse[];
    summary: ReportResponseSummary;
    pdfBase64: string;
}