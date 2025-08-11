package com.pichincha.customer.infrastructure.input.adapter.rest;

import com.pichincha.common.domain.EventType;
import com.pichincha.common.domain.LoggerAttributes;
import com.pichincha.common.domain.Strategy;
import com.pichincha.common.infrastructure.input.adapter.rest.CustomersApi;
import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.common.infrastructure.input.adapter.rest.models.CustomerWithAccount;
import com.pichincha.common.service.LoggerAuditorService;
import com.pichincha.customer.helper.WebClientHeaderHelper;
import com.pichincha.customer.infrastructure.input.port.CrudCustomerUseCase;
import com.pichincha.customer.repository.AccountService;
import com.pichincha.customer.service.AccountEventService;
import com.pichincha.customer.service.mapper.CustomerWithAccountMapper;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;

import static lombok.AccessLevel.PRIVATE;

@RestController
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CustomerController implements CustomersApi {

	private final CrudCustomerUseCase crudCustomerUseCase;
	private final CustomerWithAccountMapper customerWithAccountMapper;
	public final LoggerAuditorService loggerAuditor;
	private final AccountService accountService;

	public CustomerController(CrudCustomerUseCase crudCustomerUseCase,
			CustomerWithAccountMapper customerWithAccountMapper, LoggerAuditorService loggerAuditor,
			AccountService accountService) {
		this.crudCustomerUseCase = crudCustomerUseCase;
		this.customerWithAccountMapper = customerWithAccountMapper;
		this.loggerAuditor = loggerAuditor;
		this.accountService = accountService;
	}

	@Override
	public Mono<ResponseEntity<Customer>> createCustomer(String xGuid, String xChannel, String xMedium, String xApp,
			String xSession, Mono<Customer> customer, ServerWebExchange exchange) {
		loggerAuditor(customer, HttpStatus.CREATED, "createCustomer");
		return crudCustomerUseCase.createCustomer(customer)
				.map(createdCustomer -> ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer));
	}

	@Override
	public Mono<ResponseEntity<Void>> deleteCustomer(Integer id, String xGuid, String xChannel, String xMedium,
			String xApp, String xSession, ServerWebExchange exchange) {
		return crudCustomerUseCase.deleteCustomer(id).thenReturn(ResponseEntity.noContent().build());
	}

	@Override
	public Mono<ResponseEntity<Flux<Customer>>> getAllCustomers(String xGuid, String xChannel, String xMedium,
			String xApp, String xSession, ServerWebExchange exchange) {
		log.debug("Received request to get all customers with Request ID: {}", xGuid);
		return Mono.just(ResponseEntity.ok(crudCustomerUseCase.getAllCustomers()));
	}

	@Override
	public Mono<ResponseEntity<Customer>> getCustomerById(Integer id, String xGuid, String xChannel, String xMedium,
			String xApp, String xSession, ServerWebExchange exchange) {

		log.debug("Starting getCustomerById operation with ID: {} and Request ID: {}", id, xGuid);

		return crudCustomerUseCase.getCustomerById(id, xGuid, xChannel, xMedium, xApp).map(customer -> {
			loggerAuditor(customer, HttpStatus.OK, "getCustomerById");
			return ResponseEntity.ok(customer);
		}).onErrorResume(ex -> {
			log.error("Error retrieving customer with ID {}: {}", id, ex.getMessage(), ex);
			return Mono.error(ex);
		});
	}

	@Override
	public Mono<ResponseEntity<Customer>> updateCustomer(Integer id, String xGuid, String xChannel, String xMedium,
			String xApp, String xSession, Mono<Customer> customer, ServerWebExchange exchange) {
		return crudCustomerUseCase.updateCustomer(id, customer).map(ResponseEntity::ok);
	}

	@Override
	public Mono<ResponseEntity<CustomerWithAccount>> createCustomerWithAccount(String xGuid, String xChannel,
			String xMedium, String xApp, String xSession, Mono<CustomerWithAccount> customerWithAccount,
			ServerWebExchange exchange) {
		String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		String jwt = token != null && token.startsWith("Bearer ") ? token.substring(7) : null;
		WebClientHeaderHelper headers = new WebClientHeaderHelper(xGuid, xChannel, xMedium, xApp, xSession, jwt);
		return customerWithAccount.flatMap(cwa -> processCustomerWithAccountCreation(cwa, headers))
				.map(result -> ResponseEntity.status(HttpStatus.CREATED).body(result))
				.onErrorResume(this::handleErrorException);
	}

	private Mono<CustomerWithAccount> processCustomerWithAccountCreation(CustomerWithAccount customerWithAccountData,
			WebClientHeaderHelper headers) {
		return crudCustomerUseCase.createCustomer(Mono.just(customerWithAccountData.getCustomer()))
				.flatMap(createdCustomer -> {
					customerWithAccountData.getAccount().setCustomerId(createdCustomer.getId());
					return accountService.createAccountReactive(customerWithAccountData.getAccount(), headers)
							.map(createdAccount -> new CustomerWithAccount().customer(createdCustomer)
									.account(createdAccount));
				});
	}

	private Mono<ResponseEntity<CustomerWithAccount>> handleErrorException(Throwable ex) {
		log.error("Error creating customer with account: {}", ex.getMessage(), ex);
		return Mono.error(ex);
	}

	private void loggerAuditor(Object personDto, HttpStatus httpStatus, String nameMethod) {
		SecureRandom secureRandomString = new SecureRandom();
		int idTransactionalString = secureRandomString.nextInt();
		loggerAuditor.log(LoggerAttributes.builder().type(EventType.REQUEST).object(personDto)
				.transactionId(String.valueOf(idTransactionalString)).component(nameMethod + idTransactionalString)
				.mode(Strategy.EXTERNAL).statusCode(httpStatus.value()).build());
	}

}