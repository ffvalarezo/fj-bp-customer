package com.pichincha.customer.repository;

import com.pichincha.customer.domain.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Customer Repository interface for reactive operations.
 * Using manual implementation with in-memory data for now.
 */
public interface CustomerRepository {

  /**
   * Find all customers
   */
  Flux<Customer> findAll();

  /**
   * Find customer by ID
   */
  Mono<Customer> findById(Long id);

  /**
   * Find customer by customer ID
   */
  Mono<Customer> findByCustomerId(String customerId);

  /**
   * Find customer by identification
   */
  Mono<Customer> findByIdentification(String identification);

  /**
   * Find customer by email
   */
  Mono<Customer> findByEmail(String email);

  /**
   * Find customers by active status
   */
  Flux<Customer> findByActive(Boolean active);

  /**
   * Save customer
   */
  Mono<Customer> save(Customer customer);

  /**
   * Delete customer by ID
   */
  Mono<Void> deleteById(Long id);

  /**
   * Check if customer exists by customer ID
   */
  Mono<Boolean> existsByCustomerId(String customerId);

  /**
   * Check if customer exists by identification
   */
  Mono<Boolean> existsByIdentification(String identification);

  /**
   * Check if customer exists by email
   */
  Mono<Boolean> existsByEmail(String email);
}
