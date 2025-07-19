package com.pichincha.customer.service.impl;

import com.pichincha.customer.domain.Customer;
import com.pichincha.customer.exception.CustomerNotFoundException;
import com.pichincha.customer.repository.CustomerRepository;
import com.pichincha.customer.service.CustomerService;
import com.pichincha.customer.util.CustomerConstants;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Resilient customer service implementation with Resilience4j patterns
 * Implements Circuit Breaker, Retry, and Rate Limiter for fault tolerance
 */
@Slf4j
@Service("resilientCustomerService")
@RequiredArgsConstructor
public class ResilientCustomerService implements CustomerService {

  private static final Random RANDOM = new Random();
  
  private final CustomerRepository customerRepository;
  private final CircuitBreaker customerServiceCircuitBreaker;
  private final Retry customerServiceRetry;
  private final RateLimiter customerServiceRateLimiter;

  @Override
  public Flux<Customer> getAllCustomers() {
    log.info("Retrieving all customers with resilience patterns");
    
    return customerRepository.findAll()
        .transformDeferred(RateLimiterOperator.of(customerServiceRateLimiter))
        .transformDeferred(RetryOperator.of(customerServiceRetry))
        .transformDeferred(CircuitBreakerOperator.of(customerServiceCircuitBreaker))
        .doOnNext(customer -> log.debug("Retrieved customer: {}", customer.getCustomerId()))
        .doOnError(e -> log.error("Error retrieving customers with resilience patterns: {}", e.getMessage()))
        .onErrorResume(e -> {
          log.error("Circuit breaker opened or rate limit exceeded for getAllCustomers", e);
          return Flux.empty();
        });
  }

  @Override
  public Mono<Customer> getCustomerById(String customerId) {
    log.info("Retrieving customer with ID: {} using resilience patterns", customerId);
    
    return customerRepository.findByCustomerId(customerId)
        .transformDeferred(RateLimiterOperator.of(customerServiceRateLimiter))
        .transformDeferred(RetryOperator.of(customerServiceRetry))
        .transformDeferred(CircuitBreakerOperator.of(customerServiceCircuitBreaker))
        .switchIfEmpty(Mono.error(new CustomerNotFoundException(CustomerConstants.CUSTOMER_NOT_FOUND_MSG + customerId)))
        .doOnSuccess(customer -> log.info("Found customer with resilience: {}", customer.getDisplayName()))
        .doOnError(e -> log.error("Error finding customer {} with resilience patterns: {}", customerId, e.getMessage()))
        .onErrorMap(throwable -> {
          if (throwable instanceof CustomerNotFoundException) {
            return throwable;
          }
          log.error("Circuit breaker or retry failed for getCustomerById", throwable);
          return new RuntimeException("Customer retrieval service temporarily unavailable", throwable);
        });
  }

  @Override
  public Mono<Customer> createCustomer(Customer customer) {
    log.info("Creating new customer with resilience patterns: {}", customer.getFullName());
    
    // Generate customer ID and set timestamps
    String customerId = generateCustomerId();
    customer.setCustomerId(customerId);
    customer.setCreatedAt(LocalDateTime.now());
    customer.setUpdatedAt(LocalDateTime.now());
    
    return customerRepository.save(customer)
        .transformDeferred(RateLimiterOperator.of(customerServiceRateLimiter))
        .transformDeferred(RetryOperator.of(customerServiceRetry))
        .transformDeferred(CircuitBreakerOperator.of(customerServiceCircuitBreaker))
        .doOnSuccess(savedCustomer -> log.info("Customer created successfully with resilience: {}", savedCustomer.getCustomerId()))
        .doOnError(e -> log.error("Error creating customer with resilience patterns: {}", e.getMessage()))
        .onErrorMap(throwable -> {
          log.error("Circuit breaker or retry failed for createCustomer", throwable);
          return new RuntimeException("Customer creation service temporarily unavailable", throwable);
        });
  }

  @Override
  public Mono<Customer> updateCustomer(String customerId, Customer customer) {
    log.info("Updating customer with ID: {} using resilience patterns", customerId);
    
    return customerRepository.findByCustomerId(customerId)
        .transformDeferred(RateLimiterOperator.of(customerServiceRateLimiter))
        .transformDeferred(RetryOperator.of(customerServiceRetry))
        .transformDeferred(CircuitBreakerOperator.of(customerServiceCircuitBreaker))
        .switchIfEmpty(Mono.error(new CustomerNotFoundException(CustomerConstants.CUSTOMER_NOT_FOUND_MSG + customerId)))
        .flatMap(existingCustomer -> {
          // Update fields
          updateCustomerFields(existingCustomer, customer);
          existingCustomer.setUpdatedAt(LocalDateTime.now());
          
          return customerRepository.save(existingCustomer)
              .transformDeferred(RateLimiterOperator.of(customerServiceRateLimiter))
              .transformDeferred(RetryOperator.of(customerServiceRetry))
              .transformDeferred(CircuitBreakerOperator.of(customerServiceCircuitBreaker));
        })
        .doOnSuccess(updatedCustomer -> log.info("Customer updated successfully with resilience: {}", updatedCustomer.getCustomerId()))
        .doOnError(e -> log.error("Error updating customer {} with resilience patterns: {}", customerId, e.getMessage()))
        .onErrorMap(throwable -> {
          if (throwable instanceof CustomerNotFoundException) {
            return throwable;
          }
          log.error("Circuit breaker or retry failed for updateCustomer", throwable);
          return new RuntimeException("Customer update service temporarily unavailable", throwable);
        });
  }

  @Override
  public Mono<Void> deleteCustomer(String customerId) {
    log.info("Deleting customer with ID: {} using resilience patterns", customerId);
    
    return customerRepository.findByCustomerId(customerId)
        .transformDeferred(RateLimiterOperator.of(customerServiceRateLimiter))
        .transformDeferred(RetryOperator.of(customerServiceRetry))
        .transformDeferred(CircuitBreakerOperator.of(customerServiceCircuitBreaker))
        .switchIfEmpty(Mono.error(new CustomerNotFoundException(CustomerConstants.CUSTOMER_NOT_FOUND_MSG + customerId)))
        .flatMap(customer -> customerRepository.deleteById(customer.getPersonId())
            .transformDeferred(RateLimiterOperator.of(customerServiceRateLimiter))
            .transformDeferred(RetryOperator.of(customerServiceRetry))
            .transformDeferred(CircuitBreakerOperator.of(customerServiceCircuitBreaker)))
        .doOnSuccess(v -> log.info("Customer deleted successfully with resilience: {}", customerId))
        .doOnError(e -> log.error("Error deleting customer {} with resilience patterns: {}", customerId, e.getMessage()))
        .onErrorMap(throwable -> {
          if (throwable instanceof CustomerNotFoundException) {
            return throwable;
          }
          log.error("Circuit breaker or retry failed for deleteCustomer", throwable);
          return new RuntimeException("Customer deletion service temporarily unavailable", throwable);
        });
  }

  @Override
  public Mono<Boolean> existsById(String customerId) {
    log.debug("Checking if customer exists with resilience patterns: {}", customerId);
    
    return customerRepository.existsByCustomerId(customerId)
        .transformDeferred(RateLimiterOperator.of(customerServiceRateLimiter))
        .transformDeferred(RetryOperator.of(customerServiceRetry))
        .transformDeferred(CircuitBreakerOperator.of(customerServiceCircuitBreaker))
        .doOnError(e -> log.error("Error checking customer existence with resilience patterns: {}", e.getMessage()))
        .onErrorReturn(false);
  }

  @Override
  public Flux<Customer> getActiveCustomers() {
    log.info("Retrieving active customers with resilience patterns");
    
    return customerRepository.findByActive(true)
        .transformDeferred(RateLimiterOperator.of(customerServiceRateLimiter))
        .transformDeferred(RetryOperator.of(customerServiceRetry))
        .transformDeferred(CircuitBreakerOperator.of(customerServiceCircuitBreaker))
        .doOnNext(customer -> log.debug("Retrieved active customer: {}", customer.getCustomerId()))
        .doOnError(e -> log.error("Error retrieving active customers with resilience patterns: {}", e.getMessage()))
        .onErrorResume(e -> {
          log.error("Circuit breaker opened or rate limit exceeded for getActiveCustomers", e);
          return Flux.empty();
        });
  }

  @Override
  public Mono<Customer> activateCustomer(String customerId) {
    log.info("Activating customer with resilience patterns: {}", customerId);
    
    return customerRepository.findByCustomerId(customerId)
        .transformDeferred(RateLimiterOperator.of(customerServiceRateLimiter))
        .transformDeferred(RetryOperator.of(customerServiceRetry))
        .transformDeferred(CircuitBreakerOperator.of(customerServiceCircuitBreaker))
        .switchIfEmpty(Mono.error(new CustomerNotFoundException(CustomerConstants.CUSTOMER_NOT_FOUND_MSG + customerId)))
        .flatMap(customer -> {
          customer.activateCustomer();
          return customerRepository.save(customer)
              .transformDeferred(RateLimiterOperator.of(customerServiceRateLimiter))
              .transformDeferred(RetryOperator.of(customerServiceRetry))
              .transformDeferred(CircuitBreakerOperator.of(customerServiceCircuitBreaker));
        })
        .doOnSuccess(customer -> log.info("Customer activated with resilience: {}", customer.getCustomerId()))
        .doOnError(e -> log.error("Error activating customer {} with resilience patterns: {}", customerId, e.getMessage()))
        .onErrorMap(throwable -> {
          if (throwable instanceof CustomerNotFoundException) {
            return throwable;
          }
          log.error("Circuit breaker or retry failed for activateCustomer", throwable);
          return new RuntimeException("Customer activation service temporarily unavailable", throwable);
        });
  }

  @Override
  public Mono<Customer> deactivateCustomer(String customerId) {
    log.info("Deactivating customer with resilience patterns: {}", customerId);
    
    return customerRepository.findByCustomerId(customerId)
        .transformDeferred(RateLimiterOperator.of(customerServiceRateLimiter))
        .transformDeferred(RetryOperator.of(customerServiceRetry))
        .transformDeferred(CircuitBreakerOperator.of(customerServiceCircuitBreaker))
        .switchIfEmpty(Mono.error(new CustomerNotFoundException(CustomerConstants.CUSTOMER_NOT_FOUND_MSG + customerId)))
        .flatMap(customer -> {
          customer.deactivateCustomer();
          return customerRepository.save(customer)
              .transformDeferred(RateLimiterOperator.of(customerServiceRateLimiter))
              .transformDeferred(RetryOperator.of(customerServiceRetry))
              .transformDeferred(CircuitBreakerOperator.of(customerServiceCircuitBreaker));
        })
        .doOnSuccess(customer -> log.info("Customer deactivated with resilience: {}", customer.getCustomerId()))
        .doOnError(e -> log.error("Error deactivating customer {} with resilience patterns: {}", customerId, e.getMessage()))
        .onErrorMap(throwable -> {
          if (throwable instanceof CustomerNotFoundException) {
            return throwable;
          }
          log.error("Circuit breaker or retry failed for deactivateCustomer", throwable);
          return new RuntimeException("Customer deactivation service temporarily unavailable", throwable);
        });
  }

  /**
   * Updates customer fields from another customer object
   */
  private void updateCustomerFields(Customer target, Customer source) {
    if (source.getFullName() != null) target.setFullName(source.getFullName());
    if (source.getGender() != null) target.setGender(source.getGender());
    if (source.getAge() != null) target.setAge(source.getAge());
    if (source.getIdentification() != null) target.setIdentification(source.getIdentification());
    if (source.getAddress() != null) target.setAddress(source.getAddress());
    if (source.getCelular() != null) target.setCelular(source.getCelular());
    if (source.getEmail() != null) target.setEmail(source.getEmail());
    if (source.getPassword() != null) target.setPassword(source.getPassword());
    if (source.getActive() != null) target.setActive(source.getActive());
    if (source.getStatus() != null) target.setStatus(source.getStatus());
  }

  /**
   * Generates a unique customer ID
   */
  private String generateCustomerId() {
    return CustomerConstants.CUSTOMER_ID_PREFIX + String.format("%03d", RANDOM.nextInt(1000));
  }
}
