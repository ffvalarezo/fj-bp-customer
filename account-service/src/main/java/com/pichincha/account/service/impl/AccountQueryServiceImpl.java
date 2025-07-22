package com.pichincha.account.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pichincha.account.repository.AccountRepository;
import com.pichincha.account.service.AccountQueryService;
import com.pichincha.account.service.mapper.AccountMapper;
import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccountQueryServiceImpl implements AccountQueryService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountMapper accountMapper;

	@Override
	public Mono<AccountRequest> getAccountById(Integer id) {
		return accountRepository.findById(Long.valueOf(id)).map(accountMapper::toDto)
				.switchIfEmpty(Mono.error(new RuntimeException("Customer not found")));
	}

	@Override
	public Flux<AccountRequest> getAllAccounts() {
		return accountRepository.findAll().map(accountMapper::toDto);
	}
}