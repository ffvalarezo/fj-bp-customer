package com.pichincha.account.service.impl;

import com.pichincha.account.service.AccountCommandService;
import com.pichincha.account.service.AccountSyncKafkaListenerService;
import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.features.events", havingValue = "true")
public class AccountSyncKafkaListenerServiceImpl implements AccountSyncKafkaListenerService {

    private final AccountCommandService accountCommandService;

    @KafkaListener(topics = "${logging.transactional.kafka.topic.accountSync}", groupId = "account-sync-group")
    public void consumeAccountSync(AccountRequest accountRequest) {
        accountCommandService.createAccount(Mono.just(accountRequest));
    }
}