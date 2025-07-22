package com.pichincha.account.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pichincha.account.domain.AccountEntity;
import com.pichincha.account.repository.AccountRepository;
import com.pichincha.account.service.AccountCommandService;
import com.pichincha.account.service.mapper.AccountMapper;
import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;

import reactor.core.publisher.Mono;

@Service
public class AccountCommandServiceImpl implements AccountCommandService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private AccountMapper accountMapper;

	@Override
	public Mono<AccountRequest> createAccount(Mono<AccountRequest> request) {
		return request.flatMap(account -> {
			AccountEntity entity = accountMapper.toEntity(account);
			entity.setCreatedAt(LocalDateTime.now());
			entity.setUpdatedAt(LocalDateTime.now());
			return accountRepository.save(entity).map(accountMapper::toDto);
		});
	}

	@Override
	public Mono<AccountRequest> updateAccount(Integer id, Mono<AccountRequest> request) {
		return accountRepository.findById(Long.valueOf(id)).flatMap(existingEntity -> request.map(account -> {
			accountMapper.updateEntityFromRequest(account, existingEntity);
			existingEntity.setUpdatedAt(LocalDateTime.now());
			return existingEntity;
		})).flatMap(accountRepository::save).map(accountMapper::toDto)
				.switchIfEmpty(Mono.error(new RuntimeException("Account not found")));
	}

	@Override
	public Mono<Void> deleteAccount(Integer id) {
		return accountRepository.findById(Long.valueOf(id))
				.flatMap(existingEntity -> accountRepository.delete(existingEntity))
				.switchIfEmpty(Mono.error(new RuntimeException("Account not found")));
	}
}