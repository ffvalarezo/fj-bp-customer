package com.pichincha.customer.infrastructure.input.port.impl;

import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.customer.infrastructure.input.port.CrudCustomerUseCase;
import com.pichincha.customer.service.CustomerCommandService;
import com.pichincha.customer.service.CustomerQueryService;
import com.pichincha.customer.service.impl.CustomerCircuitBrekerService;
import com.pichincha.customer.util.Constants;
import com.pichincha.customer.util.TransactionalLogUtil;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
public class CrudCustomerUseCaseImpl implements CrudCustomerUseCase {

	private final CustomerCommandService commandService;
	private final CustomerQueryService queryService;
	private final CustomerCircuitBrekerService circuitBreakerService;

	public CrudCustomerUseCaseImpl(CustomerCommandService commandService, CustomerQueryService queryService,
			CustomerCircuitBrekerService circuitBreakerService) {
		this.commandService = commandService;
		this.queryService = queryService;
		this.circuitBreakerService = circuitBreakerService;
	}

	@Override
	public Mono<Customer> createCustomer(Mono<Customer> request) {
		return request.flatMap(customer -> commandService.createCustomer(Mono.just(customer)).onErrorResume(error -> {
			String errorMessage = Constants.ERROR_CREATE_CUSTOMER + ": " + error.getMessage();
			return switch (error) {
			case ConnectException e ->
				Mono.error(new RuntimeException(Constants.ERROR_COMMUNICATION + ": " + errorMessage));
			case TimeoutException e -> Mono.error(new RuntimeException(Constants.ERROR_TIMEOUT + ": " + errorMessage));
			case CallNotPermittedException e ->
				Mono.error(new RuntimeException(Constants.ERROR_CIRCUIT_BREAKER + ": " + errorMessage));
			default -> Mono.error(new RuntimeException(errorMessage));
			};
		}));
	}

	@Override
	public Mono<Customer> updateCustomer(Integer id, Mono<Customer> request) {
		return request.flatMap(customer -> commandService.updateCustomer(id, Mono.just(customer)))
				.onErrorResume(error -> {
					String errorMessage = Constants.ERROR_UPDATE_CUSTOMER + ": " + error.getMessage();
					return switch (error) {
					case CallNotPermittedException e ->
						Mono.error(new RuntimeException(Constants.ERROR_CIRCUIT_BREAKER + ": " + errorMessage));
					case TimeoutException e ->
						Mono.error(new RuntimeException(Constants.ERROR_TIMEOUT + ": " + errorMessage));
					case ConnectException e ->
						Mono.error(new RuntimeException(Constants.ERROR_COMMUNICATION + ": " + errorMessage));
					default -> Mono.error(new RuntimeException(errorMessage));
					};
				});
	}

	@Override
	public Mono<Void> deleteCustomer(Integer id) {
		return commandService.deleteCustomer(id).onErrorResume(error -> {
			String errorMessage = Constants.ERROR_DELETE_CUSTOMER + ": " + error.getMessage();
			return switch (error) {
			case CallNotPermittedException e ->
				Mono.error(new RuntimeException(Constants.ERROR_CIRCUIT_BREAKER + ": " + errorMessage));
			case TimeoutException e -> Mono.error(new RuntimeException(Constants.ERROR_TIMEOUT + ": " + errorMessage));
			case ConnectException e ->
				Mono.error(new RuntimeException(Constants.ERROR_COMMUNICATION + ": " + errorMessage));
			default -> Mono.error(new RuntimeException(errorMessage));
			};
		}).then();
	}

	@Override
	public Mono<Customer> getCustomerById(Integer id, String xGuid, String xChannel, String xMedium, String xApp) {
		TransactionalLogUtil.logTransactionStart("getCustomerById", xGuid, id.toString(), xChannel, xApp);

		return circuitBreakerService.getCustomerByIdWithCircuitBreaker(id)
				.doOnNext(customer -> log.debug("Customer retrieved successfully: {}", customer)).map(customer -> {
					TransactionalLogUtil.logTransactionSuccess("getCustomerById", xGuid, customer.getIdentification(),
							customer.getFullName());
					return customer;
				}).doOnError(throwable -> {
					String errorType = throwable.getClass().getSimpleName();
					String errorMessage = throwable.getMessage();
					log.error("Error occurred during getCustomerById operation: {} - {}", errorType, errorMessage);
					TransactionalLogUtil.logTransactionError("getCustomerById", xGuid, id.toString(), errorType,
							errorMessage, xChannel);
					TransactionalLogUtil.logCircuitBreakerActivation("getCustomerById", xGuid, Constants.ERROR_DETECTED,
							errorType);
				})
				.doFinally(signalType -> log.info(
						"Transaction Log [CUSTOMER_SERVICE] - Completed getCustomerById operation. "
								+ "Request ID: {}, Customer ID: {}, Signal Type: {}, Channel: {}",
						xGuid, id, signalType, xChannel));
	}

	@Override
	public Flux<Customer> getAllCustomers() {
		return queryService.getAllCustomers().retry(3).onErrorResume(error -> {
			String errorMessage = Constants.ERROR_GET_ALL_CUSTOMERS + ": " + error.getMessage();
			return switch (error) {
			case CallNotPermittedException e ->
				Flux.error(new RuntimeException(Constants.ERROR_CIRCUIT_BREAKER + ": " + errorMessage));
			case TimeoutException e -> Flux.error(new RuntimeException(Constants.ERROR_TIMEOUT + ": " + errorMessage));
			case ConnectException e ->
				Flux.error(new RuntimeException(Constants.ERROR_COMMUNICATION + ": " + errorMessage));
			default -> Flux.error(new RuntimeException(errorMessage));
			};
		});
	}

	@Override
	public Mono<Customer> getCustomerByIdentification(String identification) {
		TransactionalLogUtil.logTransactionStart("getCustomerByIdentification", identification, identification, null,
				null);
		return circuitBreakerService.getCustomerByIdentificationWithCircuitBreaker(identification)
				.doOnNext(customer -> log.debug("Customer retrieved: {}", customer)).map(customer -> {
					TransactionalLogUtil.logTransactionSuccess("getCustomerByIdentification", identification,
							customer.getIdentification(), customer.getFullName());
					return customer;
				}).doOnError(error -> {
					String errorType = error.getClass().getSimpleName();
					String errorMessage = error.getMessage();
					log.error("Error in getCustomerByIdentification: {} - {}", errorType, errorMessage);
					TransactionalLogUtil.logTransactionError("getCustomerByIdentification", identification,
							identification, errorType, errorMessage, null);
					TransactionalLogUtil.logCircuitBreakerActivation("getCustomerByIdentification", identification,
							Constants.ERROR_DETECTED, errorType);
				})
				.doFinally(signalType -> log
						.info("Transaction Log [CUSTOMER_SERVICE] - Completed getCustomerByIdentification. "
								+ "Identification: {}, Signal Type: {}", identification, signalType))
				.onErrorResume(error -> {
					String errorMessage = Constants.ERROR_GET_CUSTOMER_BY_IDENTIFICATION + ": " + error.getMessage();
					return switch (error) {
					case CallNotPermittedException e ->
						Mono.error(new RuntimeException(Constants.ERROR_CIRCUIT_BREAKER + ": " + errorMessage));
					case TimeoutException e ->
						Mono.error(new RuntimeException(Constants.ERROR_TIMEOUT + ": " + errorMessage));
					case ConnectException e ->
						Mono.error(new RuntimeException(Constants.ERROR_COMMUNICATION + ": " + errorMessage));
					default -> Mono.error(new RuntimeException(errorMessage));
					};
				});
	}
}
