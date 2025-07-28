package com.pichincha.movement.service;

import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementRequest;
import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementResponse;

import reactor.core.publisher.Mono;

public interface MovementCommandService {

	Mono<MovementResponse> createMovement(Mono<MovementRequest> request);

	Mono<MovementResponse> updateMovement(Long id, Mono<MovementRequest> request);

	Mono<Void> deleteMovement(Integer id);

}
