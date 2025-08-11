package com.pichincha.account.service;

import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;

public interface AccountSyncKafkaListenerService {

    void consumeAccountSync(AccountRequest accountRequest);
}
