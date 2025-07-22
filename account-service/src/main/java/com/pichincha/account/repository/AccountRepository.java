package com.pichincha.account.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.pichincha.account.domain.AccountEntity;

public interface AccountRepository extends ReactiveCrudRepository<AccountEntity, Long> {
}