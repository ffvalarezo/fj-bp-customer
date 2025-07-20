package com.pichincha.customer.service;

import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;

import reactor.core.publisher.Mono;

public interface CustomerCommandService {

	Mono<Customer> createCustomer(Mono<Customer> request);

  Mono<Customer> updateCustomer(Integer id, Mono<Customer> request);

  Mono<Void> deleteCustomer(Integer id);

}
