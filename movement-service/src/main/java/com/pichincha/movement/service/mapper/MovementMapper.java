package com.pichincha.movement.service.mapper;

import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementRequest;
import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementResponse;
import com.pichincha.movement.domain.MovementEntity;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", imports = { java.time.ZoneOffset.class })
public interface MovementMapper {

	MovementEntity toEntity(MovementRequest dto);

	@Mapping(target = "date", source = "date", qualifiedByName = "localDateTimeToOffsetDateTime")
	@Mapping(target = "type", source = "movementType")
	MovementResponse toDto(MovementEntity entity);

	List<MovementResponse> toDtoList(List<MovementEntity> list);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateEntityFromRequest(MovementRequest dto, @MappingTarget MovementEntity entity);

	@org.mapstruct.Named("localDateTimeToOffsetDateTime")
	default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime value) {
		return value == null ? null : value.atOffset(ZoneOffset.UTC);
	}
}