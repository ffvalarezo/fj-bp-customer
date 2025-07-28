package com.pichincha.movement.repository;

import java.time.LocalDate;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.pichincha.movement.domain.MovementEntity;

import reactor.core.publisher.Flux;

public interface MovementRepository extends ReactiveCrudRepository<MovementEntity, Long> {

  Flux<MovementEntity> findByAccountNumber(String accountNumber, LocalDate startDate, LocalDate endDate);
}