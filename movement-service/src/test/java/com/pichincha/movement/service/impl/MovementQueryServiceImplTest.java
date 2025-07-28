package com.pichincha.movement.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementResponse;
import com.pichincha.movement.domain.MovementEntity;
import com.pichincha.movement.domain.model.MovementType;
import com.pichincha.movement.repository.MovementRepository;
import com.pichincha.movement.service.mapper.MovementMapper;

import reactor.core.publisher.Flux;

class MovementQueryServiceImplTest {

	@Mock
	private MovementMapper movementMapper;

	@Mock
	private MovementRepository movementRepository;

	@InjectMocks
	private MovementQueryServiceImpl movementQueryService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void shouldMapMovementsCorrectly() {
		String accountNumber = "1234567890";
		LocalDate startDate = LocalDate.of(2022, 2, 1);
		LocalDate endDate = LocalDate.of(2022, 2, 28);
		MovementEntity movement = new MovementEntity();
		movement.setId(1L);
		movement.setDate(LocalDateTime.now());
		movement.setBalance(BigDecimal.valueOf(100.0));
		movement.setAccountNumber("1234567890");
		movement.setMovementType(MovementType.DEBIT);
		movement.setDate(LocalDateTime.of(2022, 2, 10, 10, 0));

		MovementResponse expectedResponse = new MovementResponse();
		expectedResponse.setDate(LocalDateTime.now().atOffset(ZoneOffset.UTC));
		expectedResponse.setAccountNumber(accountNumber);
		expectedResponse.setType("DEBIT");
		expectedResponse.setBalance(BigDecimal.valueOf(100.0));

		when(movementRepository.findByAccountNumber(accountNumber, startDate, endDate)).thenReturn(Flux.just(movement));
		when(movementMapper.toDto(movement)).thenReturn(expectedResponse);

		List<MovementResponse> responses = movementQueryService
				.getMovementsByAccountNumber(accountNumber, startDate, endDate).collectList().block();

		assertEquals(1, responses.size());
		assertEquals(expectedResponse, responses.get(0));
		verify(movementRepository, times(1)).findByAccountNumber(accountNumber, startDate, endDate);
		verify(movementMapper, times(1)).toDto(movement);
	}
}