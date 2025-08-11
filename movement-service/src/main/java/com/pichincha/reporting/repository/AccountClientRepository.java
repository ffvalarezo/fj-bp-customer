package com.pichincha.reporting.repository;

import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;
import com.pichincha.movement.helper.WebClientHeaderHelper;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AccountClientRepository {
    Mono<List<AccountRequest>> getAccountsByCustomerId(String customerId, WebClientHeaderHelper headers);
}