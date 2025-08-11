package com.pichincha.customer.repository;

import com.pichincha.customer.domain.CustomerEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveCrudRepository<CustomerEntity, Long> {

    Mono<CustomerEntity> findByIdentification(String identification);
}