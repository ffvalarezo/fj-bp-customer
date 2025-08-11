import { AccountRequest } from '../interfaces/account-request.interface';

export type CreateDtoAccount = Partial<Omit<AccountRequest, 'id'>>;