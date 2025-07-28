package com.pichincha.movement.service;

import java.time.LocalDate;

import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementResponse;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementQueryService {

  Mono<MovementResponse> getMovementById(Integer id);

  Flux<MovementResponse> getAllMovements();

  Flux<MovementResponse> getMovementsByAccountNumber(String accountNumber, LocalDate startDate, LocalDate endDate);

}