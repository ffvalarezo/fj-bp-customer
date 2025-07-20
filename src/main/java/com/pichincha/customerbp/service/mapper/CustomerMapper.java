package com.pichincha.customerbp.service.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.customerbp.domain.CustomerEntity;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
	@Mapping(source = "fullName", target = "name")
	@Mapping(source = "celular", target = "phone")
	@Mapping(source = "active", target = "status")
	@Mapping(target = "customerId", ignore = true)
	CustomerEntity toEntity(Customer dto);

	@Mapping(source = "name", target = "fullName")
	@Mapping(source = "phone", target = "celular")
	@Mapping(source = "status", target = "active")
	Customer toDto(CustomerEntity entity);

	List<Customer> toDtoList(List<CustomerEntity> list);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(source = "fullName", target = "name")
	@Mapping(source = "celular", target = "phone")
	@Mapping(source = "active", target = "status")
	@Mapping(target = "customerId", ignore = true)
	void updateEntityFromRequest(Customer dto, @MappingTarget CustomerEntity entity);
}