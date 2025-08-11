package com.pichincha.customer.service.impl;

import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.customer.infrastructure.exception.CustomerNotFoundException;
import com.pichincha.customer.repository.CustomerRepository;
import com.pichincha.customer.service.CustomerQueryService;
import com.pichincha.customer.service.mapper.CustomerMapper;
import com.pichincha.customer.util.Constants;
import org.springframework.stereotype.Service;
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
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(String.format(
                        Constants.CLIENT_NOT_FOUND, id))));
    }

    @Override
    public Flux<Customer> getAllCustomers() {
        return customerRepository.findAll().map(customerMapper::toDto);
    }

    @Override
    public Mono<Customer> getCustomerByIdentification(String identification) {
        return customerRepository.findByIdentification(identification)
                .map(customerMapper::toDto)
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(String.format(
                        Constants.CLIENT_ALREADY_EXISTS, identification))));
    }
}