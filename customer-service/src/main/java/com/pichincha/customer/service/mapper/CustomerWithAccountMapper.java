package com.pichincha.customer.service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.pichincha.common.infrastructure.input.adapter.rest.models.CustomerWithAccount;
import com.pichincha.customer.domain.CustomerWithAccountEntity;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class})
public interface CustomerWithAccountMapper {

  @Mapping(source = "customer", target = "customer")
  @Mapping(source = "account", target = "account", ignore = true)
  CustomerWithAccountEntity toEntity(CustomerWithAccount dto);

  @Mapping(source = "customer", target = "customer")
  @Mapping(source = "account", target = "account", ignore = true)
  CustomerWithAccount toDto(CustomerWithAccountEntity entity);
}