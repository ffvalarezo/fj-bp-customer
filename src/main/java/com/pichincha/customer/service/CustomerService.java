package com.pichincha.customer.service;

import com.pichincha.customer.domain.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Customer service interface defining business operations for Customer management
 */
public interface CustomerService {

  /**
   * Get all customers
   */
  Flux<Customer> getAllCustomers();

  /**
   * Get customer by ID
   */
  Mono<Customer> getCustomerById(String customerId);

  /**
   * Create a new customer
   */
  Mono<Customer> createCustomer(Customer customer);

  /**
   * Update an existing customer
   */
  Mono<Customer> updateCustomer(String customerId, Customer customer);

  /**
   * Delete customer by ID
   */
  Mono<Void> deleteCustomer(String customerId);

  /**
   * Check if customer exists by ID
   */
  Mono<Boolean> existsById(String customerId);

  /**
   * Get active customers only
   */
  Flux<Customer> getActiveCustomers();

  /**
   * Activate customer
   */
  Mono<Customer> activateCustomer(String customerId);

  /**
   * Deactivate customer
   */
  Mono<Customer> deactivateCustomer(String customerId);
}
