package com.pichincha.movement.service.impl;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementResponse;
import com.pichincha.movement.repository.MovementRepository;
import com.pichincha.movement.service.MovementQueryService;
import com.pichincha.movement.service.mapper.MovementMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MovementQueryServiceImpl implements MovementQueryService {

  private final MovementRepository movementRepository;
  private final MovementMapper movementMapper;

  public MovementQueryServiceImpl(MovementRepository movementRepository, MovementMapper movementMapper) {
    this.movementRepository = movementRepository;
    this.movementMapper = movementMapper;
  }

	@Override
	public Mono<MovementResponse> getMovementById(Integer id) {
		return movementRepository.findById(Long.valueOf(id)).map(movementMapper::toDto)
				.switchIfEmpty(Mono.error(new RuntimeException("Movement not found")));
	}

	@Override
	public Flux<MovementResponse> getAllMovements() {
		return movementRepository.findAll().map(movementMapper::toDto);
	}

	@Override
	public Flux<MovementResponse> getMovementsByAccountNumber(String accountNumber, LocalDate startDate, LocalDate endDate) {
		return movementRepository.findByAccountNumber(accountNumber, startDate, endDate).map(movementMapper::toDto);
	}
}