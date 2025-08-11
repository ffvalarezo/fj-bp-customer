import { Component, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { MovementService } from '../../services/movement.service';
import { ReportResponse } from '../../interfaces/report-response.interface';
import { CustomerService } from '../../services/customer.service';
import { CommonModule } from '@angular/common';
import {
    PichinchaTypographyModule,
    PichinchaDatePickerModule,
    PichinchaButtonModule,
} from '@pichincha/ds-angular';
import { Customer } from '../../interfaces/customer.interface';

@Component({
    selector: 'app-report-item',
    standalone: true,
    imports: [
        CommonModule,
        PichinchaTypographyModule,
        PichinchaDatePickerModule,
        PichinchaButtonModule,
    ],
    providers: [],
    styleUrls: ['./report-item.component.scss'],
    templateUrl: './report-item.component.html',
})
export class ReportItemComponent implements OnInit {
    report = signal<ReportResponse | undefined>(undefined);
    private movementService = inject(MovementService);
    private customerService = inject(CustomerService);
    customerId = signal<string | undefined>(undefined);
    startDate: string = '2021-01-01';
    endDate: string = '2025-12-31';
    customerResponse = signal<Customer | undefined>(undefined);
    loadingData = signal<boolean>(true);
    private router = inject(Router);

    ngOnInit(): void {
        this.loadingData.set(false);
        this.customerId.set(sessionStorage.getItem('customerId') ?? undefined);
        this.generateReport();
    }

    generateReport(): void {
        const customerId = Number(this.customerId());
        this.customerService.getCustomerById(customerId).subscribe(customer => {
            this.customerResponse.set(customer);
        });
    }
    handleReport() {
        if (this.startDate && this.endDate) {
            const customerId = Number(this.customerId());
            const params = {
                startDate: this.startDate,
                endDate: this.endDate,
                customerId: customerId.toString(),
            };
            this.movementService.movementsReportGet(params).subscribe((data: ReportResponse) => {
                this.customerService.getCustomerById(customerId).subscribe(customer => {
                    (data as any).customer = customer;
                    this.report.set(data);
                    console.log(this.report());
                });
            });
        }
        this.loadingData.set(true);
    }
    getMovementsForAccount(accountNumber: string) {
        const report = this.report();
        if (!report?.accounts || !report?.movements) return [];
        const account = report.accounts.find(acc => acc.accountNumber === accountNumber);
        if (!account) return [];
        return report.movements.filter(mov => mov.accountNumber === accountNumber);
    }

    handleReturnAccount() {
        sessionStorage.removeItem('accountId');
        this.router.navigate(['/movimiento']);
    }
}
