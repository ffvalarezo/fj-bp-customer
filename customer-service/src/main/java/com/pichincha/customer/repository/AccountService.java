package com.pichincha.customer.repository;

import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;
import com.pichincha.customer.helper.WebClientHeaderHelper;
import reactor.core.publisher.Mono;

public interface AccountService {

    Mono<AccountRequest> createAccountReactive(AccountRequest accountRequest, WebClientHeaderHelper headers);

}
