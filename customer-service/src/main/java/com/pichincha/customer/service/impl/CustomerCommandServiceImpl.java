package com.pichincha.customer.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.customer.domain.CustomerEntity;
import com.pichincha.customer.repository.CustomerRepository;
import com.pichincha.customer.service.CustomerCommandService;
import com.pichincha.customer.service.mapper.CustomerMapper;

import reactor.core.publisher.Mono;

@Service
public class CustomerCommandServiceImpl implements CustomerCommandService {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CustomerMapper customerMapper;

	@Override
	public Mono<Customer> createCustomer(Mono<Customer> request) {
		return request.flatMap(customer -> {
			CustomerEntity entity = customerMapper.toEntity(customer);
			entity.setCreatedAt(LocalDateTime.now());
			entity.setUpdatedAt(LocalDateTime.now());
			return customerRepository.save(entity).map(customerMapper::toDto);
		});
	}

	@Override
	public Mono<Customer> updateCustomer(Integer id, Mono<Customer> request) {
		return customerRepository.findById(Long.valueOf(id)).flatMap(existingEntity -> request.map(customer -> {
			customerMapper.updateEntityFromRequest(customer, existingEntity);
			existingEntity.setUpdatedAt(LocalDateTime.now());
			return existingEntity;
		})).flatMap(customerRepository::save).map(customerMapper::toDto)
				.switchIfEmpty(Mono.error(new RuntimeException("Customer not found")));
	}

	@Override
	public Mono<Void> deleteCustomer(Integer id) {
		return customerRepository.findById(Long.valueOf(id))
				.flatMap(existingEntity -> customerRepository.delete(existingEntity))
				.switchIfEmpty(Mono.error(new RuntimeException("Customer not found")));
	}
}