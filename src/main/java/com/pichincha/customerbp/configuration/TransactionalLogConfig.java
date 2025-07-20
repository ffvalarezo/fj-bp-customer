package com.pichincha.customerbp.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "logging.transactional.enabled", havingValue = "false", matchIfMissing = true)
public class TransactionalLogConfig {

}
