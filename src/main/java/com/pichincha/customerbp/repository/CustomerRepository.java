package com.pichincha.customerbp.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.pichincha.customerbp.domain.CustomerEntity;

public interface CustomerRepository extends ReactiveCrudRepository<CustomerEntity, Long> {
}