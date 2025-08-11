package com.pichincha.movement.service.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementResponse;
import com.pichincha.movement.domain.MovementEntity;
import com.pichincha.movement.domain.model.MovementType;

class MovementMapperTest {

	private final MovementMapper mapper = Mappers.getMapper(MovementMapper.class);

	@Test
	void testToDto() {
		MovementEntity entity = new MovementEntity();
		entity.setId(1L);
		entity.setCreatedAt(LocalDateTime.of(2025, 7, 22, 10, 0));
		entity.setMovementType(MovementType.CREDIT);
		entity.setValue(BigDecimal.valueOf(1600.0));
		entity.setBalance(BigDecimal.valueOf(200.0));
		entity.setAccountNumber("1234567890");

		MovementResponse response = mapper.toDto(entity);

		assertEquals(OffsetDateTime.of(2025, 7, 22, 10, 0, 0, 0, ZoneOffset.UTC), response.getDate());
		assertEquals("CREDIT", response.getType());
		assertEquals(BigDecimal.valueOf(1600.0), response.getValue());
		assertEquals(BigDecimal.valueOf(200.0), response.getBalance());
		assertEquals("1234567890", response.getAccountNumber());
	}
}