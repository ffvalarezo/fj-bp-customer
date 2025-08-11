package com.pichincha.customer.service;

import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;
import reactor.core.publisher.Mono;

public interface AccountEventService {

    Mono<Void> sendAccountRequestMessage(AccountRequest accountRequest);

}