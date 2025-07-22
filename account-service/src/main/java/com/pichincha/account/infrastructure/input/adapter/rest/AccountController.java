package com.pichincha.account.infrastructure.input.adapter.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.pichincha.account.service.AccountCommandService;
import com.pichincha.account.service.AccountQueryService;
import com.pichincha.common.infrastructure.input.adapter.rest.AccountsApi;
import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
	public Mono<ResponseEntity<AccountRequest>> createAccount(
			@NotNull @Pattern(regexp = "^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$") @Size(max = 60) String xGuid,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xChannel,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xMedium,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 5) String xApp, @NotNull @Size(max = 100) String xSession,
			@Valid Mono<AccountRequest> accountRequest, ServerWebExchange exchange) {
		return commandService.createAccount(accountRequest)
				.map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
	}

	@Override
	public Mono<ResponseEntity<Void>> deleteAccount(Integer id,
			@NotNull @Pattern(regexp = "^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$") @Size(max = 60) String xGuid,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xChannel,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xMedium,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 5) String xApp, @NotNull @Size(max = 100) String xSession,
			ServerWebExchange exchange) {
		return commandService.deleteAccount(id).thenReturn(ResponseEntity.noContent().build());
	}

	@Override
	public Mono<ResponseEntity<AccountRequest>> getAccountById(Integer id,
			@NotNull @Pattern(regexp = "^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$") @Size(max = 60) String xGuid,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xChannel,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xMedium,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 5) String xApp, @NotNull @Size(max = 100) String xSession,
			ServerWebExchange exchange) {
		return queryService.getAccountById(id).map(ResponseEntity::ok);
	}

	@Override
	public Mono<ResponseEntity<Flux<AccountRequest>>> getAllAccounts(
			@NotNull @Pattern(regexp = "^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$") @Size(max = 60) String xGuid,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xChannel,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xMedium,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 5) String xApp, @NotNull @Size(max = 100) String xSession,
			ServerWebExchange exchange) {
		return Mono.just(ResponseEntity.ok(queryService.getAllAccounts()));
	}

	@Override
	public Mono<ResponseEntity<AccountRequest>> updateAccount(Integer id,
			@NotNull @Pattern(regexp = "^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$") @Size(max = 60) String xGuid,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xChannel,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xMedium,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 5) String xApp, @NotNull @Size(max = 100) String xSession,
			@Valid Mono<AccountRequest> accountRequest, ServerWebExchange exchange) {
		return commandService.updateAccount(id, accountRequest).map(updated -> ResponseEntity.ok(updated));
	}
}