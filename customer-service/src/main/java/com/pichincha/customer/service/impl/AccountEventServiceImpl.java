package com.pichincha.customer.service.impl;

import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;
import com.pichincha.customer.service.AccountEventService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@ConditionalOnProperty(name = "app.features.events", havingValue = "true")
@Service
public class AccountEventServiceImpl implements AccountEventService {

    private final KafkaTemplate<String, AccountRequest> kafkaTemplate;
    private final String accountSyncTopic;

    public AccountEventServiceImpl(
            KafkaTemplate<String, AccountRequest> kafkaTemplate,
            @Value("${logging.transactional.kafka.topic.accountSync}") String accountSyncTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.accountSyncTopic = accountSyncTopic;
    }

    @Override
    public Mono<Void> sendAccountRequestMessage(AccountRequest accountRequest) {
        kafkaTemplate.send(accountSyncTopic, accountRequest);
        return Mono.empty();
    }
}