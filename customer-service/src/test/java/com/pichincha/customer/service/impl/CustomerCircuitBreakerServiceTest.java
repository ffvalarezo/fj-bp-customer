package com.pichincha.customer.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import com.pichincha.customer.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.customer.infrastructure.exception.CustomerNotFoundException;
import com.pichincha.customer.service.CustomerQueryService;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class CustomerCircuitBreakerServiceTest {

	@Mock
	private CustomerQueryService customerQueryService;
	private CustomerCircuitBrekerService circuitBreakerService;

	@BeforeEach
	void setUp() {
		CircuitBreakerConfig config = CircuitBreakerConfig.custom().slidingWindowSize(5).minimumNumberOfCalls(3)
				.failureRateThreshold(50.0f).build();
		circuitBreakerService = new CustomerCircuitBrekerService(customerQueryService);
	}

	@Test
	void testGetCustomerByIdWithCircuitBreaker_Success() {
		// Given
		Integer customerId = 1;
		Customer expectedCustomer = new Customer();
		expectedCustomer.setIdentification("9999999999");
		expectedCustomer.setFullName("John Doe");
		expectedCustomer.setEmail("john.doe@email.com");

		when(customerQueryService.getCustomerById(customerId)).thenReturn(Mono.just(expectedCustomer));

		// When & Then
		StepVerifier.create(circuitBreakerService.getCustomerByIdWithCircuitBreaker(customerId))
				.expectNext(expectedCustomer).verifyComplete();
	}

	@Test
	void testGetCustomerByIdWithCircuitBreaker_NotFound() {
		// Given
		Integer customerId = 999;
		when(customerQueryService.getCustomerById(customerId))
				.thenReturn(Mono.error(new CustomerNotFoundException(customerId)));

		// When & Then
		StepVerifier.create(circuitBreakerService.getCustomerByIdWithCircuitBreaker(customerId))
				.expectError(CustomerNotFoundException.class).verify();
	}

	@Test
	void testGetCustomerByIdFallback() {
		// Given
		String customerId = "UNKNOWN";
		Exception exception = new RuntimeException("Service error");

		// When
		Mono<Customer> result = circuitBreakerService.getCustomerByIdFallback(1, exception);

		// Then
		StepVerifier.create(result).assertNext(customer -> {
			assertNotNull(customer);
			assertEquals(customerId, customer.getIdentification());
			assertEquals(Constants.FALLBACK_FULL_NAME, customer.getFullName());
			assertEquals(Constants.FALLBACK_EMAIL, customer.getEmail());
			assertEquals(Constants.FALLBACK_CELULAR, customer.getCelular());
			assertEquals(Constants.FALLBACK_ADDRESS, customer.getAddress());
			assertEquals(false, customer.getActive());
		}).verifyComplete();
	}
}
