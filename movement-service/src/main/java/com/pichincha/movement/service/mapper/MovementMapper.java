package com.pichincha.movement.service.mapper;

import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementRequest;
import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementResponse;
import com.pichincha.movement.domain.MovementEntity;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring", imports = {java.time.ZoneOffset.class})
public interface MovementMapper {

    @Mapping(target = "movementType", source = "type")
    MovementEntity toEntity(MovementRequest dto);

    @Mapping(target = "date", source = "createdAt", qualifiedByName = "localDateTimeToOffsetDateTime")
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