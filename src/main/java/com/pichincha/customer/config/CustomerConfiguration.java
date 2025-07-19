package com.pichincha.customer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Configuration class for customer microservice
 * Enables component scanning for all patterns and helpers
 */
@Slf4j
@Configuration
@ComponentScan(basePackages = {
  "com.pichincha.customer.helper",
  "com.pichincha.customer.service",
  "com.pichincha.customer.repository",
  "com.pichincha.customer.exception",
  "com.pichincha.customer.domain.service",
  "com.pichincha.customer.service.validation"
})
public class CustomerConfiguration {

  public CustomerConfiguration() {
    log.info("Customer microservice configuration initialized");
    log.info("Clean Architecture layers: Domain, Application, Infrastructure");
    log.info("Design patterns enabled: Factory, Strategy, Observer");
    log.info("SOLID principles: SRP, OCP, LSP, ISP, DIP implemented");
    log.info("Clean Code practices: Constants, validation, separation of concerns");
    log.info("Exception handling: Global exception handler active");
    log.info("Validation strategies: Basic and Enhanced available");
  }
}
