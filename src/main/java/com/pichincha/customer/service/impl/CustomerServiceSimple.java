package com.pichincha.customer.service.impl;

import com.pichincha.customer.domain.Customer;
import com.pichincha.customer.exception.CustomerNotFoundException;
import com.pichincha.customer.repository.CustomerRepository;
import com.pichincha.customer.service.CustomerService;
import com.pichincha.customer.util.CustomerConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Simple customer service implementation using Customer
 * Implements reactive patterns with CustomerRepository
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceSimple implements CustomerService {

  private static final Random RANDOM = new Random();
  
  private final CustomerRepository customerRepository;

  @Override
  public Flux<Customer> getAllCustomers() {
    log.info("Retrieving all customers");
    return customerRepository.findAll()
        .doOnError(e -> log.error("Error retrieving customers: {}", e.getMessage()))
        .onErrorResume(e -> Flux.empty());
  }

  @Override
  public Mono<Customer> getCustomerById(String customerId) {
    log.info("Retrieving customer with ID: {}", customerId);
    return customerRepository.findByCustomerId(customerId)
        .switchIfEmpty(Mono.error(new CustomerNotFoundException(CustomerConstants.CUSTOMER_NOT_FOUND_MSG + customerId)))
        .doOnSuccess(customer -> log.info("Found customer: {}", customer.getDisplayName()))
        .doOnError(e -> log.error("Error finding customer {}: {}", customerId, e.getMessage()));
  }

  @Override
  public Mono<Customer> createCustomer(Customer customer) {
    log.info("Creating new customer: {}", customer.getFullName());
    
    // Generate customer ID
    String customerId = generateCustomerId();
    customer.setCustomerId(customerId);
    customer.setCreatedAt(LocalDateTime.now());
    customer.setUpdatedAt(LocalDateTime.now());
    
    return customerRepository.save(customer)
        .doOnSuccess(savedCustomer -> log.info("Customer created successfully: {}", savedCustomer.getCustomerId()))
        .doOnError(e -> log.error("Error creating customer: {}", e.getMessage()));
  }

  @Override
  public Mono<Customer> updateCustomer(String customerId, Customer customer) {
    log.info("Updating customer with ID: {}", customerId);
    
    return customerRepository.findByCustomerId(customerId)
        .switchIfEmpty(Mono.error(new CustomerNotFoundException(CustomerConstants.CUSTOMER_NOT_FOUND_MSG + customerId)))
        .flatMap(existingCustomer -> {
          // Update fields
          existingCustomer.setFullName(customer.getFullName());
          existingCustomer.setGender(customer.getGender());
          existingCustomer.setAge(customer.getAge());
          existingCustomer.setIdentification(customer.getIdentification());
          existingCustomer.setAddress(customer.getAddress());
          existingCustomer.setCelular(customer.getCelular());
          existingCustomer.setEmail(customer.getEmail());
          existingCustomer.setUpdatedAt(LocalDateTime.now());
          
          return customerRepository.save(existingCustomer);
        })
        .doOnSuccess(updatedCustomer -> log.info("Customer updated successfully: {}", updatedCustomer.getCustomerId()))
        .doOnError(e -> log.error("Error updating customer {}: {}", customerId, e.getMessage()));
  }

  @Override
  public Mono<Void> deleteCustomer(String customerId) {
    log.info("Deleting customer with ID: {}", customerId);
    
    return customerRepository.findByCustomerId(customerId)
        .switchIfEmpty(Mono.error(new CustomerNotFoundException(CustomerConstants.CUSTOMER_NOT_FOUND_MSG + customerId)))
        .flatMap(customer -> customerRepository.deleteById(customer.getPersonId()))
        .doOnSuccess(v -> log.info("Customer deleted successfully: {}", customerId))
        .doOnError(e -> log.error("Error deleting customer {}: {}", customerId, e.getMessage()));
  }

  @Override
  public Mono<Boolean> existsById(String customerId) {
    log.debug("Checking if customer exists: {}", customerId);
    return customerRepository.existsByCustomerId(customerId);
  }

  @Override
  public Flux<Customer> getActiveCustomers() {
    log.info("Retrieving active customers");
    return customerRepository.findByActive(true)
        .doOnError(e -> log.error("Error retrieving active customers: {}", e.getMessage()))
        .onErrorResume(e -> Flux.empty());
  }

  @Override
  public Mono<Customer> activateCustomer(String customerId) {
    log.info("Activating customer: {}", customerId);
    
    return customerRepository.findByCustomerId(customerId)
        .switchIfEmpty(Mono.error(new CustomerNotFoundException(CustomerConstants.CUSTOMER_NOT_FOUND_MSG + customerId)))
        .flatMap(customer -> {
          customer.activateCustomer();
          return customerRepository.save(customer);
        })
        .doOnSuccess(customer -> log.info("Customer activated: {}", customer.getCustomerId()))
        .doOnError(e -> log.error("Error activating customer {}: {}", customerId, e.getMessage()));
  }

  @Override
  public Mono<Customer> deactivateCustomer(String customerId) {
    log.info("Deactivating customer: {}", customerId);
    
    return customerRepository.findByCustomerId(customerId)
        .switchIfEmpty(Mono.error(new CustomerNotFoundException(CustomerConstants.CUSTOMER_NOT_FOUND_MSG + customerId)))
        .flatMap(customer -> {
          customer.deactivateCustomer();
          return customerRepository.save(customer);
        })
        .doOnSuccess(customer -> log.info("Customer deactivated: {}", customer.getCustomerId()))
        .doOnError(e -> log.error("Error deactivating customer {}: {}", customerId, e.getMessage()));
  }

  /**
   * Generates a unique customer ID
   */
  private String generateCustomerId() {
    return CustomerConstants.CUSTOMER_ID_PREFIX + String.format("%03d", RANDOM.nextInt(1000));
  }
}
