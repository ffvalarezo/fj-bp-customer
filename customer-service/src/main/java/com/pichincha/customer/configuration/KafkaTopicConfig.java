package com.pichincha.customer.configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(name = "app.features.events", havingValue = "true")
@Configuration
public class KafkaTopicConfig {

    @Value("${logging.transactional.kafka.topic.accountSync}")
    private String accountSyncTopic;

    @Bean
    public String accountSyncTopic() {
        return accountSyncTopic;
    }
}