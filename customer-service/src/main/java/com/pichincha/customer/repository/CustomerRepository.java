package com.pichincha.customer.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.pichincha.customer.domain.CustomerEntity;

public interface CustomerRepository extends ReactiveCrudRepository<CustomerEntity, Long> {
}