package com.pichincha.account.repository;

import com.pichincha.account.domain.AccountEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AccountRepository extends ReactiveCrudRepository<AccountEntity, Long> {
    Flux<AccountEntity> findByCustomerId(Long customerId);

    Flux<AccountEntity> findByAccountNumber(String accountNumber);
}