package com.pichincha.customer.service.impl;

import com.pichincha.customer.infrastructure.exception.CustomerNotFoundException;
import org.springframework.stereotype.Service;

import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.customer.repository.CustomerRepository;
import com.pichincha.customer.service.CustomerQueryService;
import com.pichincha.customer.service.mapper.CustomerMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerQueryServiceImpl implements CustomerQueryService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  public CustomerQueryServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
    this.customerRepository = customerRepository;
    this.customerMapper = customerMapper;
  }

  @Override
  public Mono<Customer> getCustomerById(Integer id) {
    return customerRepository.findById(Long.valueOf(id))
        .map(customerMapper::toDto)
        .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer with ID " + id + " not found")));
  }

  @Override
  public Flux<Customer> getAllCustomers() {
    return customerRepository.findAll().map(customerMapper::toDto);
  }
}