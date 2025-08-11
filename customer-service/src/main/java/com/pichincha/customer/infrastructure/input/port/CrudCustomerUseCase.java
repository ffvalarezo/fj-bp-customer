package com.pichincha.customer.infrastructure.input.port;

import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CrudCustomerUseCase {

    Mono<Customer> createCustomer(Mono<Customer> request);

    Mono<Customer> updateCustomer(Integer id, Mono<Customer> request);

    Mono<Void> deleteCustomer(Integer id);

    Mono<Customer> getCustomerById(Integer id, String xGuid, String xChannel, String xMedium,
                                   String xApp);

    Flux<Customer> getAllCustomers();

    Mono<Customer> getCustomerByIdentification(String identification);
}
