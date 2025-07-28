package com.pichincha.movement.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementRequest;
import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementResponse;
import com.pichincha.movement.domain.MovementEntity;
import com.pichincha.movement.repository.MovementRepository;
import com.pichincha.movement.service.MovementCommandService;
import com.pichincha.movement.service.mapper.MovementMapper;

import reactor.core.publisher.Mono;

@Service
public class MovementCommandServiceImpl implements MovementCommandService {

  private final MovementRepository movementRepository;
  private final MovementMapper movementMapper;

  public MovementCommandServiceImpl(MovementRepository movementRepository, MovementMapper movementMapper) {
    this.movementRepository = movementRepository;
    this.movementMapper = movementMapper;
  }

	@Override
	public Mono<MovementResponse> createMovement(Mono<MovementRequest> request) {
		return request.flatMap(movement -> {
			MovementEntity entity = movementMapper.toEntity(movement);
			entity.setCreatedAt(LocalDateTime.now());
			entity.setUpdatedAt(LocalDateTime.now());
			return movementRepository.save(entity).map(movementMapper::toDto);
		});
	}

	@Override
	public Mono<MovementResponse> updateMovement(Long id, Mono<MovementRequest> request) {
		return movementRepository.findById(id).flatMap(existingEntity -> request.map(movement -> {
			movementMapper.updateEntityFromRequest(movement, existingEntity);
			existingEntity.setUpdatedAt(LocalDateTime.now());
			return existingEntity;
		})).flatMap(movementRepository::save).map(movementMapper::toDto)
				.switchIfEmpty(Mono.error(new RuntimeException("Movement not found")));
	}

	@Override
	public Mono<Void> deleteMovement(Integer id) {
		return movementRepository.findById(Long.valueOf(id))
				.flatMap(existingEntity -> movementRepository.delete(existingEntity))
				.switchIfEmpty(Mono.error(new RuntimeException("Movement not found")));
	}
}