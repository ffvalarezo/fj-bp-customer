package com.pichincha.customerbp.infrastructure.input.adapter.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.pichincha.common.infrastructure.input.adapter.rest.CustomersApi;
import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.customerbp.service.CustomerCommandService;
import com.pichincha.customerbp.service.CustomerQueryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class CustomerController implements CustomersApi {

	private final CustomerCommandService commandService;
	private final CustomerQueryService queryService;

	public CustomerController(CustomerCommandService commandService, CustomerQueryService queryService) {
		this.commandService = commandService;
		this.queryService = queryService;
	}

	@Override
	public Mono<ResponseEntity<Customer>> createCustomer(
			@NotNull @Pattern(regexp = "^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$") @Size(max = 60) String xGuid,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xChannel,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xMedium,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 5) String xApp, @NotNull @Size(max = 100) String xSession,
			@Valid Mono<Customer> customer, ServerWebExchange exchange) {
		return commandService.createCustomer(customer)
				.map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
	}

	@Override
	public Mono<ResponseEntity<Void>> deleteCustomer(Integer id,
			@NotNull @Pattern(regexp = "^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$") @Size(max = 60) String xGuid,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xChannel,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xMedium,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 5) String xApp, @NotNull @Size(max = 100) String xSession,
			ServerWebExchange exchange) {
		return commandService.deleteCustomer(id).thenReturn(ResponseEntity.noContent().build());
	}

	@Override
	public Mono<ResponseEntity<Flux<Customer>>> getAllCustomers(
			@NotNull @Pattern(regexp = "^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$") @Size(max = 60) String xGuid,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xChannel,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xMedium,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 5) String xApp, @NotNull @Size(max = 100) String xSession,
			ServerWebExchange exchange) {
		return Mono.just(ResponseEntity.ok(queryService.getAllCustomers()));
	}

	@Override
	public Mono<ResponseEntity<Customer>> getCustomerById(Integer id,
			@NotNull @Pattern(regexp = "^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$") @Size(max = 60) String xGuid,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xChannel,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xMedium,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 5) String xApp, @NotNull @Size(max = 100) String xSession,
			ServerWebExchange exchange) {
		return queryService.getCustomerById(id).map(ResponseEntity::ok);
	}

	@Override
	public Mono<ResponseEntity<Customer>> updateCustomer(Integer id,
			@NotNull @Pattern(regexp = "^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$") @Size(max = 60) String xGuid,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xChannel,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 2) String xMedium,
			@NotNull @Pattern(regexp = "^\\d+$") @Size(max = 5) String xApp, @NotNull @Size(max = 100) String xSession,
			@Valid Mono<Customer> customer, ServerWebExchange exchange) {
		return commandService.updateCustomer(id, customer).map(updated -> ResponseEntity.ok(updated));
	}

}