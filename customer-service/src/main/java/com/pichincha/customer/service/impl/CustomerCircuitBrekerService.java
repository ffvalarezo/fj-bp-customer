package com.pichincha.customer.service.impl;

import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.customer.infrastructure.exception.CustomerNotFoundException;
import com.pichincha.customer.service.CustomerQueryService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class CustomerCircuitBrekerService {

  private final CustomerQueryService customerQueryService;

  public CustomerCircuitBrekerService(CustomerQueryService customerQueryService) {
    this.customerQueryService = customerQueryService;
  }

  @CircuitBreaker(name = "customerService", fallbackMethod = "getCustomerByIdFallback")
  @Retry(name = "customerService")
  @TimeLimiter(name = "customerService")
  public Mono<Customer> getCustomerByIdWithCircuitBreaker(Integer id) {
    return customerQueryService.getCustomerById(id).onErrorMap(ex -> {
      if (ex instanceof CustomerNotFoundException) {
        return ex;
      }
      return new RuntimeException("Service temporarily unavailable", ex);
    });
  }

  public Mono<Customer> getCustomerByIdFallback(Integer id, Exception ex) {
    log.warn(
        "Fallback Log [CUSTOMER_SERVICE] - Circuit breaker fallback triggered for customer ID: {}, reason: {}",
        id, ex.getMessage());

    return Mono.fromCallable(() -> {
      Customer fallbackCustomer = new Customer();
      fallbackCustomer.setIdentification("9999999999");
      fallbackCustomer.setFullName("Service Unavailable");
      fallbackCustomer.setEmail("service.unavailable@banco.com");
      fallbackCustomer.setCelular("N/A");
      fallbackCustomer.setAddress("Service temporarily unavailable");
      fallbackCustomer.setActive(false);

      log.info(
          "Fallback Log [CUSTOMER_SERVICE] - Returning fallback customer for ID: {} due to circuit breaker activation",
          id);
      return fallbackCustomer;
    });
  }

}