package com.pichincha.customer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.customer.repository.CustomerRepository;
import com.pichincha.customer.service.CustomerQueryService;
import com.pichincha.customer.service.mapper.CustomerMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CustomerQueryServiceImpl implements CustomerQueryService {

	@Autowired
	private final CustomerRepository customerRepository;
	private final CustomerMapper customerMapper;

	@Override
	public Mono<Customer> getCustomerById(Integer id) {
		return customerRepository.findById(Long.valueOf(id)).map(customerMapper::toDto)
				.switchIfEmpty(Mono.error(new RuntimeException("Customer not found")));
	}

	@Override
	public Flux<Customer> getAllCustomers() {
		return customerRepository.findAll().map(customerMapper::toDto);
	}
}