package com.pichincha.customer.service;

import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerQueryService {

	Mono<Customer> getCustomerById(Integer id);

	Flux<Customer> getAllCustomers();

}