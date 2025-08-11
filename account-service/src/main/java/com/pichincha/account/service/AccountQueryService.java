package com.pichincha.account.service;

import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountQueryService {

    Mono<AccountRequest> getAccountById(Integer id);

    Flux<AccountRequest> getAllAccounts();

    Flux<AccountRequest> getAccountByCustomerId(Integer customerId);

    Flux<AccountRequest> getAccountByAccountNumber(String accountNumber);

}