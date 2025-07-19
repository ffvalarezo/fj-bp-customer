package com.pichincha.customer.domain.service;

import com.pichincha.customer.domain.Customer;
import com.pichincha.customer.domain.vo.CustomerCreationRequest;
import com.pichincha.customer.helper.CustomerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Customer domain service with resilience patterns preparation
 * Encapsulates domain business logic following DDD principles
 * Prepared for Circuit Breaker, Retry, and Rate Limiter patterns when dependencies are available
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerDomainService {

  private final CustomerFactory customerFactory;

  /**
   * Creates a new customer from creation request with resilience readiness
   * Follows domain-driven design principles with fault tolerance preparation
   */
  public Mono<Customer> createCustomer(CustomerCreationRequest request) {
    log.debug("Creating customer through domain service for email: {}", request.getEmail());
    
    return Mono.fromCallable(() -> {
      log.debug("Executing customer creation with resilience preparation");
      return customerFactory.createCustomer(
        request.getFirstName(),
        request.getLastName(),
        request.getEmail(),
        request.getPhoneNumber(),
        request.getDocumentId(),
        request.getPassword()
      );
    })
    .timeout(Duration.ofSeconds(5)) // Basic timeout as resilience preparation
    .doOnSuccess(customer -> log.info("Customer created successfully with ID: {}", customer.getCustomerId()))
    .doOnError(throwable -> log.error("Failed to create customer for email: {}", request.getEmail(), throwable))
    .onErrorMap(throwable -> {
      log.error("Customer creation failed", throwable);
      return new RuntimeException("Customer creation service temporarily unavailable", throwable);
    });
  }

  /**
   * Validates if customer can be created with error handling
   */
  public Mono<Boolean> canCreateCustomer(CustomerCreationRequest request) {
    log.debug("Validating customer creation request for email: {}", request.getEmail());
    
    return Mono.fromCallable(() -> {
      // Business rules for customer creation
      boolean isValid = request != null && 
                       request.getEmail() != null && 
                       request.getFirstName() != null &&
                       request.getEmail().contains("@") &&
                       request.getFirstName().trim().length() > 0;
      
      log.debug("Customer validation result: {}", isValid);
      return isValid;
    })
    .timeout(Duration.ofSeconds(3)) // Basic timeout
    .doOnSuccess(valid -> log.debug("Customer validation completed: {}", valid))
    .doOnError(throwable -> log.error("Customer validation failed", throwable))
    .onErrorReturn(false);
  }

  /**
   * Enhanced customer creation with comprehensive validation and resilience
   */
  public Mono<Customer> createCustomerWithValidation(CustomerCreationRequest request) {
    log.info("Starting customer creation with validation for email: {}", request.getEmail());
    
    return canCreateCustomer(request)
        .filter(Boolean::booleanValue)
        .switchIfEmpty(Mono.error(new IllegalArgumentException("Customer validation failed")))
        .flatMap(valid -> createCustomer(request))
        .doOnSuccess(customer -> log.info("Customer created and validated successfully: {}", customer.getCustomerId()))
        .doOnError(throwable -> log.error("Failed to create customer with validation", throwable));
  }

  /**
   * Simulates circuit breaker pattern with manual error handling
   * This method demonstrates how circuit breaker would work
   */
  public Mono<Customer> createCustomerWithCircuitBreakerSimulation(CustomerCreationRequest request) {
    log.info("Creating customer with circuit breaker simulation for email: {}", request.getEmail());
    
    return createCustomer(request)
        .retry(2) // Basic retry mechanism
        .timeout(Duration.ofSeconds(10)) // Timeout simulation
        .doOnSuccess(customer -> log.info("Customer created with circuit breaker simulation: {}", customer.getCustomerId()))
        .doOnError(throwable -> log.error("Circuit breaker simulation - operation failed", throwable))
        .onErrorMap(throwable -> {
          log.error("Circuit breaker simulation - service degraded", throwable);
          return new RuntimeException("Service temporarily unavailable - circuit breaker open", throwable);
        });
  }
}
