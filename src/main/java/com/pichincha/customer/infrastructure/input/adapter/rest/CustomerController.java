package com.pichincha.customer.infrastructure.input.adapter.rest;

import com.pichincha.common.infrastructure.input.adapter.rest.CustomersApi;
import com.pichincha.common.infrastructure.input.adapter.rest.models.CreateCustomerRequest;
import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.common.infrastructure.input.adapter.rest.models.UpdateCustomerRequest;
import com.pichincha.customer.service.CustomerService;
import com.pichincha.customer.service.mapper.CustomerMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller implementation for Customer API following Clean Architecture principles
 * Implements the generated CustomersApi interface from OpenAPI specification
 */
@RestController
public class CustomerController implements CustomersApi {

  private final CustomerService customerService;
  private final CustomerMapper customerMapper;

  public CustomerController(CustomerService customerService, CustomerMapper customerMapper) {
    this.customerService = customerService;
    this.customerMapper = customerMapper;
  }

  /**
   * Create a new customer
   */
  @Override
  public Mono<ResponseEntity<Customer>> createCustomer(
      String xGuid,
      String xChannel,
      String xMedium,
      String xApp,
      String xSession,
      Mono<CreateCustomerRequest> createCustomerRequest,
      ServerWebExchange exchange) {

    return createCustomerRequest
        .map(customerMapper::toDomain)
        .flatMap(customerService::createCustomer)
        .map(customerMapper::toResponse)
        .map(customer -> ResponseEntity.status(HttpStatus.CREATED).body(customer))
        .onErrorReturn(ResponseEntity.status(HttpStatus.BAD_REQUEST).build());
  }

  /**
   * Delete customer by ID
   */
  @Override
  public Mono<ResponseEntity<Void>> deleteCustomer(
      Integer id,
      String xGuid,
      String xChannel,
      String xMedium,
      String xApp,
      String xSession,
      ServerWebExchange exchange) {

    return customerService.deleteCustomer(String.valueOf(id))
        .then(Mono.just(ResponseEntity.noContent().<Void>build()))
        .onErrorReturn(ResponseEntity.notFound().build());
  }

  /**
   * Retrieve all customers
   */
  @Override
  public Mono<ResponseEntity<Flux<Customer>>> getAllCustomers(
      String xGuid,
      String xChannel,
      String xMedium,
      String xApp,
      String xSession,
      ServerWebExchange exchange) {

    return Mono.just(
        ResponseEntity.ok(
            customerService.getAllCustomers()
                .map(customerMapper::toResponse)
        )
    );
  }

  /**
   * Get customer by ID
   */
  @Override
  public Mono<ResponseEntity<Customer>> getCustomerById(
      Integer id,
      String xGuid,
      String xChannel,
      String xMedium,
      String xApp,
      String xSession,
      ServerWebExchange exchange) {

    return customerService.getCustomerById(String.valueOf(id))
        .map(customerMapper::toResponse)
        .map(ResponseEntity::ok)
        .onErrorReturn(ResponseEntity.notFound().build());
  }

  /**
   * Update an existing customer
   */
  @Override
  public Mono<ResponseEntity<Customer>> updateCustomer(
      Integer id,
      String xGuid,
      String xChannel,
      String xMedium,
      String xApp,
      String xSession,
      Mono<UpdateCustomerRequest> updateCustomerRequest,
      ServerWebExchange exchange) {

    return updateCustomerRequest
        .map(customerMapper::toDomain)
        .flatMap(customer -> customerService.updateCustomer(String.valueOf(id), customer))
        .map(customerMapper::toResponse)
        .map(ResponseEntity::ok)
        .onErrorReturn(ResponseEntity.notFound().build());
  }
}
