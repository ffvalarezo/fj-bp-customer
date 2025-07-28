package com.pichincha.account.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.pichincha.account.domain.AccountEntity;

import reactor.core.publisher.Flux;

public interface AccountRepository extends ReactiveCrudRepository<AccountEntity, Long> {
	Flux<AccountEntity> findByCustomerId(Long customerId);
}