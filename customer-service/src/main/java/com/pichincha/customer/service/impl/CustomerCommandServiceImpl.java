package com.pichincha.customer.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.customer.domain.CustomerEntity;
import com.pichincha.customer.infrastructure.exception.CustomerNotFoundException;
import com.pichincha.customer.infrastructure.exception.CustomerValidationException;
import com.pichincha.customer.repository.CustomerRepository;
import com.pichincha.customer.service.CustomerCommandService;
import com.pichincha.customer.service.mapper.CustomerMapper;

import reactor.core.publisher.Mono;

@Service
public class CustomerCommandServiceImpl implements CustomerCommandService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  public CustomerCommandServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
    this.customerRepository = customerRepository;
    this.customerMapper = customerMapper;
  }

	@Override
	public Mono<Customer> createCustomer(Mono<Customer> request) {
		return request
			.doOnNext(this::validateCustomerInput)
			.flatMap(customer -> {
				CustomerEntity entity = customerMapper.toEntity(customer);
				entity.setCreatedAt(LocalDateTime.now());
				entity.setUpdatedAt(LocalDateTime.now());
				return customerRepository.save(entity);
			})
			.map(customerMapper::toDto);
	}

	@Override
	public Mono<Customer> updateCustomer(Integer id, Mono<Customer> request) {
		return customerRepository.findById(Long.valueOf(id))
			.switchIfEmpty(Mono.error(new CustomerNotFoundException(String.valueOf(id))))
			.flatMap(existingEntity -> 
				request
					.doOnNext(this::validateCustomerInput)
					.map(customer -> {
						customerMapper.updateEntityFromRequest(customer, existingEntity);
						existingEntity.setUpdatedAt(LocalDateTime.now());
						return existingEntity;
					})
			)
			.flatMap(customerRepository::save)
			.map(customerMapper::toDto);
	}

	@Override
	public Mono<Void> deleteCustomer(Integer id) {
		return customerRepository.findById(Long.valueOf(id))
			.switchIfEmpty(Mono.error(new CustomerNotFoundException(String.valueOf(id))))
			.flatMap(customerRepository::delete);
	}
	
	private void validateCustomerInput(Customer customer) {
		if (customer == null) {
			throw new CustomerValidationException("Customer data cannot be null");
		}
		if (customer.getFullName() == null || customer.getFullName().trim().isEmpty()) {
			throw new CustomerValidationException("Customer full name is required");
		}
		if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
			throw new CustomerValidationException("Customer email is required");
		}
		if (customer.getIdentification() == null || customer.getIdentification().trim().isEmpty()) {
			throw new CustomerValidationException("Customer identification is required");
		}
	}
}