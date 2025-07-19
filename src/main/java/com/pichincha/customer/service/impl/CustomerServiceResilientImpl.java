package com.pichincha.customer.service.impl;

import com.pichincha.customer.domain.Customer;
import com.pichincha.customer.domain.vo.CustomerCreationRequest;
import com.pichincha.customer.domain.service.CustomerDomainService;
import com.pichincha.customer.repository.CustomerRepository;
import com.pichincha.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Resilient customer service implementation
 * Prepared for enterprise-grade fault tolerance patterns
 * Implements comprehensive error handling and recovery mechanisms
 */
@Slf4j
@Service("customerServiceResilient")
@RequiredArgsConstructor
public class CustomerServiceResilientImpl implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerDomainService customerDomainService;

  @Override
  public Mono<Customer> createCustomer(Customer customer) {
    log.info("Creating customer with resilient service for email: {}", customer.getEmail());
    
    return customerRepository.save(customer)
        .timeout(Duration.ofSeconds(10))
        .retry(2)
        .doOnSuccess(savedCustomer -> log.info("Customer created successfully: {}", savedCustomer.getCustomerId()))
        .doOnError(throwable -> log.error("Failed to create customer", throwable))
        .onErrorMap(throwable -> {
          log.error("Resilient service - customer creation failed", throwable);
          return new RuntimeException("Customer service temporarily unavailable", throwable);
        });
  }

  @Override
  public Mono<Customer> getCustomerById(String customerId) {
    log.debug("Retrieving customer by ID with resilient service: {}", customerId);
    
    return customerRepository.findById(Long.parseLong(customerId))
        .timeout(Duration.ofSeconds(5))
        .doOnSuccess(customer -> {
          if (customer != null) {
            log.debug("Customer retrieved successfully: {}", customerId);
          } else {
            log.debug("Customer not found: {}", customerId);
          }
        })
        .doOnError(throwable -> log.error("Failed to retrieve customer: {}", customerId, throwable))
        .onErrorMap(throwable -> {
          log.error("Resilient service - customer retrieval failed", throwable);
          return new RuntimeException("Customer retrieval service temporarily unavailable", throwable);
        });
  }

  @Override
  public Flux<Customer> getAllCustomers() {
    log.debug("Retrieving all customers with resilient service");
    
    return customerRepository.findAll()
        .timeout(Duration.ofSeconds(15))
        .doOnNext(customer -> log.trace("Retrieved customer: {}", customer.getCustomerId()))
        .doOnComplete(() -> log.debug("All customers retrieved successfully"))
        .doOnError(throwable -> log.error("Failed to retrieve all customers", throwable))
        .onErrorMap(throwable -> {
          log.error("Resilient service - all customers retrieval failed", throwable);
          return new RuntimeException("Customer listing service temporarily unavailable", throwable);
        });
  }

  @Override
  public Mono<Customer> updateCustomer(String customerId, Customer customer) {
    log.info("Updating customer with resilient service: {}", customerId);
    
    return customerRepository.findById(Long.parseLong(customerId))
        .switchIfEmpty(Mono.error(new RuntimeException("Customer not found: " + customerId)))
        .flatMap(existingCustomer -> {
          log.debug("Updating customer details for: {}", customerId);
          
          Customer updatedCustomer = Customer.builder()
              .customerId(existingCustomer.getCustomerId())
              .fullName(customer.getFullName())
              .gender(customer.getGender())
              .age(customer.getAge())
              .identification(customer.getIdentification())
              .address(customer.getAddress())
              .celular(customer.getCelular())
              .email(customer.getEmail())
              .password(customer.getPassword())
              .active(customer.getActive())
              .status(customer.getStatus())
              .createdAt(existingCustomer.getCreatedAt())
              .updatedAt(java.time.LocalDateTime.now())
              .build();
          
          return customerRepository.save(updatedCustomer);
        })
        .timeout(Duration.ofSeconds(10))
        .retry(2)
        .doOnSuccess(updatedCustomer -> log.info("Customer updated successfully: {}", customerId))
        .doOnError(throwable -> log.error("Failed to update customer: {}", customerId, throwable))
        .onErrorMap(throwable -> {
          log.error("Resilient service - customer update failed", throwable);
          return new RuntimeException("Customer update service temporarily unavailable", throwable);
        });
  }

  @Override
  public Mono<Void> deleteCustomer(String customerId) {
    log.info("Deleting customer with resilient service: {}", customerId);
    
    return customerRepository.findById(Long.parseLong(customerId))
        .switchIfEmpty(Mono.error(new RuntimeException("Customer not found: " + customerId)))
        .flatMap(customer -> {
          log.debug("Deleting customer: {}", customerId);
          return customerRepository.deleteById(Long.parseLong(customerId));
        })
        .timeout(Duration.ofSeconds(10))
        .retry(2)
        .doOnSuccess(unused -> log.info("Customer deleted successfully: {}", customerId))
        .doOnError(throwable -> log.error("Failed to delete customer: {}", customerId, throwable))
        .onErrorMap(throwable -> {
          log.error("Resilient service - customer deletion failed", throwable);
          return new RuntimeException("Customer deletion service temporarily unavailable", throwable);
        });
  }

  @Override
  public Mono<Boolean> existsById(String customerId) {
    log.debug("Checking if customer exists: {}", customerId);
    
    return customerRepository.findById(Long.parseLong(customerId))
        .map(customer -> true)
        .defaultIfEmpty(false)
        .timeout(Duration.ofSeconds(5))
        .doOnSuccess(exists -> log.debug("Customer exists check result for {}: {}", customerId, exists))
        .doOnError(throwable -> log.error("Failed to check customer existence: {}", customerId, throwable))
        .onErrorReturn(false);
  }

  @Override
  public Flux<Customer> getActiveCustomers() {
    log.debug("Retrieving active customers with resilient service");
    
    return customerRepository.findAll()
        .filter(customer -> customer.getStatus() == null || !customer.getStatus().equals("INACTIVE"))
        .timeout(Duration.ofSeconds(15))
        .doOnNext(customer -> log.trace("Retrieved active customer: {}", customer.getCustomerId()))
        .doOnComplete(() -> log.debug("Active customers retrieved successfully"))
        .doOnError(throwable -> log.error("Failed to retrieve active customers", throwable))
        .onErrorMap(throwable -> {
          log.error("Resilient service - active customers retrieval failed", throwable);
          return new RuntimeException("Active customer listing service temporarily unavailable", throwable);
        });
  }

  @Override
  public Mono<Customer> activateCustomer(String customerId) {
    log.info("Activating customer with resilient service: {}", customerId);
    
    return customerRepository.findById(Long.parseLong(customerId))
        .switchIfEmpty(Mono.error(new RuntimeException("Customer not found: " + customerId)))
        .flatMap(customer -> {
          log.debug("Activating customer: {}", customerId);
          
          Customer activatedCustomer = Customer.builder()
              .customerId(customer.getCustomerId())
              .fullName(customer.getFullName())
              .gender(customer.getGender())
              .age(customer.getAge())
              .identification(customer.getIdentification())
              .address(customer.getAddress())
              .celular(customer.getCelular())
              .email(customer.getEmail())
              .password(customer.getPassword())
              .active(true)
              .status(com.pichincha.customer.domain.enums.CustomerStatus.ACTIVE)
              .createdAt(customer.getCreatedAt())
              .updatedAt(java.time.LocalDateTime.now())
              .build();
          
          return customerRepository.save(activatedCustomer);
        })
        .timeout(Duration.ofSeconds(10))
        .retry(2)
        .doOnSuccess(customer -> log.info("Customer activated successfully: {}", customerId))
        .doOnError(throwable -> log.error("Failed to activate customer: {}", customerId, throwable))
        .onErrorMap(throwable -> {
          log.error("Resilient service - customer activation failed", throwable);
          return new RuntimeException("Customer activation service temporarily unavailable", throwable);
        });
  }

  @Override
  public Mono<Customer> deactivateCustomer(String customerId) {
    log.info("Deactivating customer with resilient service: {}", customerId);
    
    return customerRepository.findById(Long.parseLong(customerId))
        .switchIfEmpty(Mono.error(new RuntimeException("Customer not found: " + customerId)))
        .flatMap(customer -> {
          log.debug("Deactivating customer: {}", customerId);
          
          Customer deactivatedCustomer = Customer.builder()
              .customerId(customer.getCustomerId())
              .fullName(customer.getFullName())
              .gender(customer.getGender())
              .age(customer.getAge())
              .identification(customer.getIdentification())
              .address(customer.getAddress())
              .celular(customer.getCelular())
              .email(customer.getEmail())
              .password(customer.getPassword())
              .active(false)
              .status(com.pichincha.customer.domain.enums.CustomerStatus.INACTIVE)
              .createdAt(customer.getCreatedAt())
              .updatedAt(java.time.LocalDateTime.now())
              .build();
          
          return customerRepository.save(deactivatedCustomer);
        })
        .timeout(Duration.ofSeconds(10))
        .retry(2)
        .doOnSuccess(customer -> log.info("Customer deactivated successfully: {}", customerId))
        .doOnError(throwable -> log.error("Failed to deactivate customer: {}", customerId, throwable))
        .onErrorMap(throwable -> {
          log.error("Resilient service - customer deactivation failed", throwable);
          return new RuntimeException("Customer deactivation service temporarily unavailable", throwable);
        });
  }

  /**
   * Custom method to create customer from request object
   */
  public Mono<Customer> createCustomerFromRequest(CustomerCreationRequest request) {
    log.info("Creating customer from request with resilient service for email: {}", request.getEmail());
    
    return customerDomainService.createCustomerWithValidation(request)
        .flatMap(customer -> {
          log.debug("Saving customer to repository: {}", customer.getCustomerId());
          return customerRepository.save(customer);
        })
        .timeout(Duration.ofSeconds(10))
        .retry(2)
        .doOnSuccess(customer -> log.info("Customer created from request successfully: {}", customer.getCustomerId()))
        .doOnError(throwable -> log.error("Failed to create customer from request", throwable))
        .onErrorMap(throwable -> {
          log.error("Resilient service - customer creation from request failed", throwable);
          return new RuntimeException("Customer service temporarily unavailable", throwable);
        });
  }

  /**
   * Health check method for circuit breaker monitoring
   */
  public Mono<Boolean> checkServiceHealth() {
    log.debug("Checking resilient service health");
    
    return Mono.fromCallable(() -> {
      return customerRepository != null && customerDomainService != null;
    })
    .timeout(Duration.ofSeconds(3))
    .doOnSuccess(healthy -> log.debug("Service health check: {}", healthy))
    .doOnError(throwable -> log.error("Service health check failed", throwable))
    .onErrorReturn(false);
  }
}
