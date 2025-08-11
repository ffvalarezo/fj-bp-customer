package com.pichincha.customer.service.impl;

import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.customer.infrastructure.exception.CustomerNotFoundException;
import com.pichincha.customer.service.CustomerQueryService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import com.pichincha.customer.util.Constants;

@Service
@Slf4j
public class CustomerCircuitBrekerService {

    private final CustomerQueryService customerQueryService;

    public CustomerCircuitBrekerService(CustomerQueryService customerQueryService) {
        this.customerQueryService = customerQueryService;
    }

    @CircuitBreaker(name = "customerService", fallbackMethod = "getCustomerByIdFallback")
    @Retry(name = "customerService")
    @TimeLimiter(name = "customerService")
    public Mono<Customer> getCustomerByIdWithCircuitBreaker(Integer id) {
        return customerQueryService.getCustomerById(id).onErrorMap(ex -> {
            if (ex instanceof CustomerNotFoundException) {
                return ex;
            }
            return new RuntimeException("Service temporarily unavailable", ex);
        });
    }

    public Mono<Customer> getCustomerByIdFallback(Integer id, Exception ex) {
        return Mono.fromCallable(() -> {
            Customer fallbackCustomer = new Customer();
            fallbackCustomer.setIdentification(Constants.FALLBACK_IDENTIFICATION);
            fallbackCustomer.setFullName(Constants.FALLBACK_FULL_NAME);
            fallbackCustomer.setEmail(Constants.FALLBACK_EMAIL);
            fallbackCustomer.setCelular(Constants.FALLBACK_CELULAR);
            fallbackCustomer.setAddress(Constants.FALLBACK_ADDRESS);
            fallbackCustomer.setActive(Constants.FALLBACK_ACTIVE);
            return fallbackCustomer;
        });
    }

    public Mono<Customer> getCustomerByIdentificationWithCircuitBreaker(String identification) {
        return customerQueryService.getCustomerByIdentification(identification)
                .onErrorMap(ex -> {
                    if (ex instanceof CustomerNotFoundException) {
                        return ex;
                    }
                    return new RuntimeException("Service temporarily unavailable", ex);
                });
    }

}