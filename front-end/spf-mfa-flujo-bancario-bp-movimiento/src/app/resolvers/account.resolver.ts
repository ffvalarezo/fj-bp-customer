import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';
import { AccountService } from "../services/account.service";
import { AccountRequest } from '../interfaces/account-request.interface';
import { firstValueFrom } from 'rxjs';

@Injectable()
export class AccountResolver implements Resolve<AccountRequest> {
    accountService: AccountService = inject(AccountService);

    resolve(route: ActivatedRouteSnapshot, _state: RouterStateSnapshot): Promise<AccountRequest> {
        const { id } = route.params
        return firstValueFrom(this.accountService.getAccountById(id));
    }
}