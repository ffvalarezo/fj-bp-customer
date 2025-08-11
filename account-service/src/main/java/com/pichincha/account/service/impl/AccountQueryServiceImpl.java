package com.pichincha.account.service.impl;

import com.pichincha.account.domain.AccountEntity;
import com.pichincha.account.repository.AccountRepository;
import com.pichincha.account.service.AccountQueryService;
import com.pichincha.account.service.mapper.AccountMapper;
import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountQueryServiceImpl implements AccountQueryService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountQueryServiceImpl(AccountRepository accountRepository, AccountMapper accountMapper) {
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }

    @Override
    public Mono<AccountRequest> getAccountById(Integer id) {
        return accountRepository.findById(Long.valueOf(id)).map(accountMapper::toDto)
                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found")));
    }

    @Override
    public Flux<AccountRequest> getAllAccounts() {
        return accountRepository.findAll().map(accountMapper::toDto);
    }

    @Override
    public Flux<AccountRequest> getAccountByCustomerId(Integer customerId) {
        return accountRepository.findByCustomerId(Long.valueOf(customerId)).map(accountMapper::toDto);
    }

    @Override
    public Flux<AccountRequest> getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).map(accountMapper::toDto);
    }

}