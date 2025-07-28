package com.pichincha.movement.service.mapper;

import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementRequest;
import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementResponse;
import com.pichincha.movement.domain.MovementEntity;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor"
)
@Component
public class MovementMapperImpl implements MovementMapper {

    @Override
    public MovementEntity toEntity(MovementRequest dto) {
        if ( dto == null ) {
            return null;
        }

        MovementEntity movementEntity = new MovementEntity();

        movementEntity.setAccountNumber( dto.getAccountNumber() );
        if ( dto.getValue() != null ) {
            movementEntity.setValue( BigDecimal.valueOf( dto.getValue() ) );
        }

        return movementEntity;
    }

    @Override
    public MovementResponse toDto(MovementEntity entity) {
        if ( entity == null ) {
            return null;
        }

        MovementResponse movementResponse = new MovementResponse();

        movementResponse.setDate( localDateTimeToOffsetDateTime( entity.getDate() ) );
        if ( entity.getMovementType() != null ) {
            movementResponse.type( entity.getMovementType().name() );
        }
        movementResponse.setAccountNumber( entity.getAccountNumber() );
        movementResponse.setBalance( entity.getBalance() );
        if ( entity.getId() != null ) {
            movementResponse.setId( String.valueOf( entity.getId() ) );
        }
        movementResponse.value( entity.getValue() );

        return movementResponse;
    }

    @Override
    public List<MovementResponse> toDtoList(List<MovementEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<MovementResponse> list1 = new ArrayList<MovementResponse>( list.size() );
        for ( MovementEntity movementEntity : list ) {
            list1.add( toDto( movementEntity ) );
        }

        return list1;
    }

    @Override
    public void updateEntityFromRequest(MovementRequest dto, MovementEntity entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getAccountNumber() != null ) {
            entity.setAccountNumber( dto.getAccountNumber() );
        }
        if ( dto.getValue() != null ) {
            entity.setValue( BigDecimal.valueOf( dto.getValue() ) );
        }
    }
}
