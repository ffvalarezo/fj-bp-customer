package com.pichincha.account.infrastructure.input.adapter.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.pichincha.account.service.AccountCommandService;
import com.pichincha.account.service.AccountQueryService;
import com.pichincha.common.infrastructure.input.adapter.rest.AccountsApi;
import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class AccountController implements AccountsApi {

	private final AccountCommandService commandService;
	private final AccountQueryService queryService;

	public AccountController(AccountCommandService commandService, AccountQueryService queryService) {
		this.commandService = commandService;
		this.queryService = queryService;
	}

	@Override
	public Mono<ResponseEntity<AccountRequest>> createAccount(String xGuid, String xChannel, String xMedium,
			String xApp, String xSession, Mono<AccountRequest> accountRequest, ServerWebExchange exchange) {
		return commandService.createAccount(accountRequest)
				.map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
	}

	@Override
	public Mono<ResponseEntity<Void>> deleteAccount(Integer id, String xGuid, String xChannel, String xMedium,
			String xApp, String xSession, ServerWebExchange exchange) {
		return commandService.deleteAccount(id).thenReturn(ResponseEntity.noContent().build());
	}

	@Override
	public Mono<ResponseEntity<AccountRequest>> getAccountById(Integer id, String xGuid, String xChannel,
			String xMedium, String xApp, String xSession, ServerWebExchange exchange) {
		return queryService.getAccountById(id).map(ResponseEntity::ok);
	}

	@Override
	public Mono<ResponseEntity<Flux<AccountRequest>>> getAccountsByCustomerId(Integer customerId, String xGuid,
			String xChannel, String xMedium, String xApp, String xSession, ServerWebExchange exchange) {
		return Mono.just(ResponseEntity.ok(queryService.getAccountByCustomerId(customerId)));
	}

	@Override
	public Mono<ResponseEntity<Flux<AccountRequest>>> getAllAccounts(String xGuid, String xChannel, String xMedium,
			String xApp, String xSession, ServerWebExchange exchange) {
		return Mono.just(ResponseEntity.ok(queryService.getAllAccounts()));
	}

	@Override
	public Mono<ResponseEntity<AccountRequest>> updateAccount(Integer id, String xGuid, String xChannel, String xMedium,
			String xApp, String xSession, Mono<AccountRequest> accountRequest, ServerWebExchange exchange) {
		return commandService.updateAccount(id, accountRequest).map(updated -> ResponseEntity.ok(updated));
	}

}