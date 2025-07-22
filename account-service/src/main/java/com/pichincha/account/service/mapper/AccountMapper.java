package com.pichincha.account.service.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.pichincha.account.domain.AccountEntity;
import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;

@Mapper(componentModel = "spring")
public interface AccountMapper {
	
	AccountEntity toEntity(AccountRequest dto);

	AccountRequest toDto(AccountEntity entity);

	List<AccountRequest> toDtoList(List<AccountEntity> list);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateEntityFromRequest(AccountRequest dto, @MappingTarget AccountEntity entity);
	
}