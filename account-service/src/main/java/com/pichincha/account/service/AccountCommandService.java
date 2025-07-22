package com.pichincha.account.service;

import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;

import reactor.core.publisher.Mono;

public interface AccountCommandService {

	Mono<AccountRequest> createAccount(Mono<AccountRequest> request);

	Mono<AccountRequest> updateAccount(Integer id, Mono<AccountRequest> request);

	Mono<Void> deleteAccount(Integer id);

}
