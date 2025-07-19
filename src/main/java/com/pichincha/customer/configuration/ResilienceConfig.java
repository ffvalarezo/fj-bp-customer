package com.pichincha.customer.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Resilience4j configuration for Customer Service
 * Uses Spring Boot auto-configuration with specific bean instances
 * Following Banco Pichincha standards for fault tolerance
 */
@Slf4j
@Configuration
public class ResilienceConfig {

  // Service names constants
  private static final String CUSTOMER_SERVICE = "customerService";
  private static final String DATABASE_SERVICE = "database";
  private static final String CLIENTE_SERVICE = "clienteService";

  /**
   * Customer Service Circuit Breaker
   * Uses application.yml configuration via Spring Boot auto-configuration
   */
  @Bean
  public CircuitBreaker customerServiceCircuitBreaker(CircuitBreakerRegistry registry) {
    CircuitBreaker circuitBreaker = registry.circuitBreaker(CUSTOMER_SERVICE);
    
    // Add event listeners for monitoring
    circuitBreaker.getEventPublisher().onStateTransition(event ->
        log.info("Circuit Breaker {} state transition: {} -> {}", 
            circuitBreaker.getName(), 
            event.getStateTransition().getFromState(), 
            event.getStateTransition().getToState()));
    
    return circuitBreaker;
  }

  /**
   * Cliente Service Circuit Breaker
   */
  @Bean
  public CircuitBreaker clienteServiceCircuitBreaker(CircuitBreakerRegistry registry) {
    CircuitBreaker circuitBreaker = registry.circuitBreaker(CLIENTE_SERVICE);
    
    circuitBreaker.getEventPublisher().onStateTransition(event ->
        log.info("Circuit Breaker {} state transition: {} -> {}", 
            circuitBreaker.getName(), 
            event.getStateTransition().getFromState(), 
            event.getStateTransition().getToState()));
    
    return circuitBreaker;
  }

  /**
   * Database Circuit Breaker
   */
  @Bean
  public CircuitBreaker databaseCircuitBreaker(CircuitBreakerRegistry registry) {
    CircuitBreaker circuitBreaker = registry.circuitBreaker(DATABASE_SERVICE);
    
    circuitBreaker.getEventPublisher().onStateTransition(event ->
        log.info("Circuit Breaker {} state transition: {} -> {}", 
            circuitBreaker.getName(), 
            event.getStateTransition().getFromState(), 
            event.getStateTransition().getToState()));
    
    return circuitBreaker;
  }

  /**
   * Customer Service Retry
   */
  @Bean
  public Retry customerServiceRetry(RetryRegistry registry) {
    Retry retry = registry.retry(CUSTOMER_SERVICE);
    
    retry.getEventPublisher().onRetry(event ->
        log.warn("Retry attempt {} for {}: {}", 
            event.getNumberOfRetryAttempts(), 
            CUSTOMER_SERVICE, 
            event.getLastThrowable().getMessage()));
    
    return retry;
  }

  /**
   * Cliente Service Retry
   */
  @Bean
  public Retry clienteServiceRetry(RetryRegistry registry) {
    Retry retry = registry.retry(CLIENTE_SERVICE);
    
    retry.getEventPublisher().onRetry(event ->
        log.warn("Retry attempt {} for {}: {}", 
            event.getNumberOfRetryAttempts(), 
            CLIENTE_SERVICE, 
            event.getLastThrowable().getMessage()));
    
    return retry;
  }

  /**
   * Customer Service Rate Limiter
   */
  @Bean
  public RateLimiter customerServiceRateLimiter(RateLimiterRegistry registry) {
    RateLimiter rateLimiter = registry.rateLimiter(CUSTOMER_SERVICE);
    
    rateLimiter.getEventPublisher().onFailure(event ->
        log.warn("Rate Limiter {} rejected call", CUSTOMER_SERVICE));
    
    return rateLimiter;
  }

  /**
   * Cliente Service Rate Limiter
   */
  @Bean
  public RateLimiter clienteServiceRateLimiter(RateLimiterRegistry registry) {
    RateLimiter rateLimiter = registry.rateLimiter(CLIENTE_SERVICE);
    
    rateLimiter.getEventPublisher().onFailure(event ->
        log.warn("Rate Limiter {} rejected call", CLIENTE_SERVICE));
    
    return rateLimiter;
  }

  /**
   * Customer Service Time Limiter
   */
  @Bean
  public TimeLimiter customerServiceTimeLimiter(TimeLimiterRegistry registry) {
    TimeLimiter timeLimiter = registry.timeLimiter(CUSTOMER_SERVICE);
    
    timeLimiter.getEventPublisher().onTimeout(event ->
        log.warn("Time Limiter {} timeout occurred", CUSTOMER_SERVICE));
    
    return timeLimiter;
  }

  /**
   * Cliente Service Time Limiter
   */
  @Bean
  public TimeLimiter clienteServiceTimeLimiter(TimeLimiterRegistry registry) {
    TimeLimiter timeLimiter = registry.timeLimiter(CLIENTE_SERVICE);
    
    timeLimiter.getEventPublisher().onTimeout(event ->
        log.warn("Time Limiter {} timeout occurred", CLIENTE_SERVICE));
    
    return timeLimiter;
  }

  /**
   * Customer Service Bulkhead
   */
  @Bean
  public Bulkhead customerServiceBulkhead(BulkheadRegistry registry) {
    Bulkhead bulkhead = registry.bulkhead(CUSTOMER_SERVICE);
    
    bulkhead.getEventPublisher().onCallRejected(event ->
        log.warn("Bulkhead {} rejected call", CUSTOMER_SERVICE));
    
    return bulkhead;
  }

  /**
   * Cliente Service Bulkhead
   */
  @Bean
  public Bulkhead clienteServiceBulkhead(BulkheadRegistry registry) {
    Bulkhead bulkhead = registry.bulkhead(CLIENTE_SERVICE);
    
    bulkhead.getEventPublisher().onCallRejected(event ->
        log.warn("Bulkhead {} rejected call", CLIENTE_SERVICE));
    
    return bulkhead;
  }
}
