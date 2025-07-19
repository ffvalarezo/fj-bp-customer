package com.pichincha.customer.repository.impl;

import com.pichincha.customer.domain.Customer;
import com.pichincha.customer.domain.enums.CustomerStatus;
import com.pichincha.customer.repository.CustomerRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory implementation of CustomerRepository.
 * This is a temporary implementation to make the application work without database setup.
 */
@Repository
public class InMemoryCustomerRepository implements CustomerRepository {

  private final ConcurrentHashMap<Long, Customer> customers = new ConcurrentHashMap<>();
  private final AtomicLong idGenerator = new AtomicLong(1);

  public InMemoryCustomerRepository() {
    // Initialize with sample data
    initializeSampleData();
  }

  private void initializeSampleData() {
    Customer customer1 = Customer.builder()
        .personId(1L)
        .fullName("Juan Pérez")
        .gender("MALE")
        .age(30)
        .identification("1234567890")
        .address("Av. Amazonas 123")
        .celular("0987654321")
        .email("juan.perez@email.com")
        .customerId("CUST001")
        .password("pass1234")
        .active(true)
        .status(CustomerStatus.ACTIVE)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    Customer customer2 = Customer.builder()
        .personId(2L)
        .fullName("María García")
        .gender("FEMALE")
        .age(25)
        .identification("0987654321")
        .address("Av. 6 de Diciembre 456")
        .celular("0912345678")
        .email("maria.garcia@email.com")
        .customerId("CUST002")
        .password("pass5678")
        .active(true)
        .status(CustomerStatus.ACTIVE)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    Customer customer3 = Customer.builder()
        .personId(3L)
        .fullName("Carlos López")
        .gender("MALE")
        .age(35)
        .identification("1122334455")
        .address("Av. Pichincha 789")
        .celular("0998877665")
        .email("carlos.lopez@email.com")
        .customerId("CUST003")
        .password("pass9012")
        .active(false)
        .status(CustomerStatus.INACTIVE)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    customers.put(1L, customer1);
    customers.put(2L, customer2);
    customers.put(3L, customer3);
    idGenerator.set(4L);
  }

  @Override
  public Flux<Customer> findAll() {
    return Flux.fromIterable(customers.values());
  }

  @Override
  public Mono<Customer> findById(Long id) {
    return Mono.justOrEmpty(customers.get(id));
  }

  @Override
  public Mono<Customer> findByCustomerId(String customerId) {
    return Flux.fromIterable(customers.values())
        .filter(customer -> customerId.equals(customer.getCustomerId()))
        .next();
  }

  @Override
  public Mono<Customer> findByIdentification(String identification) {
    return Flux.fromIterable(customers.values())
        .filter(customer -> identification.equals(customer.getIdentification()))
        .next();
  }

  @Override
  public Mono<Customer> findByEmail(String email) {
    return Flux.fromIterable(customers.values())
        .filter(customer -> email.equals(customer.getEmail()))
        .next();
  }

  @Override
  public Flux<Customer> findByActive(Boolean active) {
    return Flux.fromIterable(customers.values())
        .filter(customer -> active.equals(customer.getActive()));
  }

  @Override
  public Mono<Customer> save(Customer customer) {
    if (customer.getPersonId() == null) {
      customer.setPersonId(idGenerator.getAndIncrement());
      customer.setCreatedAt(LocalDateTime.now());
    }
    customer.setUpdatedAt(LocalDateTime.now());
    customers.put(customer.getPersonId(), customer);
    return Mono.just(customer);
  }

  @Override
  public Mono<Void> deleteById(Long id) {
    customers.remove(id);
    return Mono.empty();
  }

  @Override
  public Mono<Boolean> existsByCustomerId(String customerId) {
    return findByCustomerId(customerId)
        .map(customer -> true)
        .defaultIfEmpty(false);
  }

  @Override
  public Mono<Boolean> existsByIdentification(String identification) {
    return findByIdentification(identification)
        .map(customer -> true)
        .defaultIfEmpty(false);
  }

  @Override
  public Mono<Boolean> existsByEmail(String email) {
    return findByEmail(email)
        .map(customer -> true)
        .defaultIfEmpty(false);
  }
}
