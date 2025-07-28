package com.pichincha.customer.infrastructure.input.adapter.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.pichincha.common.infrastructure.input.adapter.rest.CustomersApi;
import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.customer.service.CustomerCommandService;
import com.pichincha.customer.service.CustomerQueryService;
import com.pichincha.customer.service.impl.CustomerCircuitBrekerService;
import com.pichincha.customer.util.TransactionalLogUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class CustomerController implements CustomersApi {

	private final CustomerCommandService commandService;
	private final CustomerQueryService queryService;
	private final CustomerCircuitBrekerService circuitBreakerService;

	public CustomerController(CustomerCommandService commandService, CustomerQueryService queryService,
			CustomerCircuitBrekerService circuitBreakerService) {
		this.commandService = commandService;
		this.queryService = queryService;
		this.circuitBreakerService = circuitBreakerService;
	}

	@Override
	public Mono<ResponseEntity<Customer>> createCustomer(String xGuid, String xChannel, String xMedium, String xApp,
			String xSession, Mono<Customer> customer, ServerWebExchange exchange) {
		return commandService.createCustomer(customer)
				.map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
	}

	@Override
	public Mono<ResponseEntity<Void>> deleteCustomer(Integer id, String xGuid, String xChannel, String xMedium,
			String xApp, String xSession, ServerWebExchange exchange) {
		return commandService.deleteCustomer(id).thenReturn(ResponseEntity.noContent().build());
	}

	@Override
	public Mono<ResponseEntity<Flux<Customer>>> getAllCustomers(String xGuid, String xChannel, String xMedium,
			String xApp, String xSession, ServerWebExchange exchange) {
		return Mono.just(ResponseEntity.ok(queryService.getAllCustomers()));
	}

	@Override
	public Mono<ResponseEntity<Customer>> getCustomerById(Integer id, String xGuid, String xChannel, String xMedium,
			String xApp, String xSession, ServerWebExchange exchange) {

		log.debug("Starting getCustomerById operation with ID: {} and Request ID: {}", id, xGuid);

		TransactionalLogUtil.logTransactionStart("getCustomerById", xGuid, id.toString(), xChannel, xApp);

		return circuitBreakerService.getCustomerByIdWithCircuitBreaker(id)
				.doOnNext(customer -> log.debug("Customer retrieved successfully: {}", customer))
				.map(customer -> {
					TransactionalLogUtil.logTransactionSuccess("getCustomerById", xGuid, customer.getIdentification(),
							customer.getFullName());
					return ResponseEntity.ok(customer);
				}).doOnError(throwable -> {
					String errorType = throwable.getClass().getSimpleName();
					String errorMessage = throwable.getMessage();

					log.error("Error occurred during getCustomerById operation: {} - {}", errorType, errorMessage);

					TransactionalLogUtil.logTransactionError("getCustomerById", xGuid, id.toString(), errorType,
							errorMessage, xChannel);
					TransactionalLogUtil.logCircuitBreakerActivation("getCustomerById", xGuid, "ERROR_DETECTED",
							errorType);
				}).doFinally(signalType -> log.info(
						"Transaction Log [CUSTOMER_SERVICE] - Completed getCustomerById operation. "
								+ "Request ID: {}, Customer ID: {}, Signal Type: {}, Channel: {}",
						xGuid, id, signalType, xChannel));
	}

	@Override
	public Mono<ResponseEntity<Customer>> updateCustomer(Integer id, String xGuid, String xChannel, String xMedium,
			String xApp, String xSession, Mono<Customer> customer, ServerWebExchange exchange) {
		return commandService.updateCustomer(id, customer).map(updated -> ResponseEntity.ok(updated));
	}

}