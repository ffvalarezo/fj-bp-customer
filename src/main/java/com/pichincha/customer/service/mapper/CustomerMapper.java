package com.pichincha.customer.service.mapper;

import com.pichincha.common.infrastructure.input.adapter.rest.models.CreateCustomerRequest;
import com.pichincha.common.infrastructure.input.adapter.rest.models.Customer;
import com.pichincha.common.infrastructure.input.adapter.rest.models.UpdateCustomerRequest;
import com.pichincha.customer.domain.enums.CustomerStatus;
import com.pichincha.customer.util.CustomerConstants;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

import java.time.LocalDateTime;

/**
 * Mapper for converting between domain models and OpenAPI generated models
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper {

  /**
   * Maps CreateCustomerRequest to domain Customer
   */
  @Mappings({
      @Mapping(target = "customerId", ignore = true),
      @Mapping(target = "personId", ignore = true),
      @Mapping(target = "status", source = "request", qualifiedByName = "setActiveStatus"),
      @Mapping(target = "active", constant = "true"),
      @Mapping(target = "createdAt", source = "request", qualifiedByName = "setCurrentTime"),
      @Mapping(target = "updatedAt", source = "request", qualifiedByName = "setCurrentTime")
  })
  com.pichincha.customer.domain.Customer toDomain(CreateCustomerRequest request);

  /**
   * Maps domain Customer to OpenAPI Customer response
   */
  @Mappings({
      @Mapping(source = "customerId", target = "customerId", qualifiedByName = "mapCustomerIdToInteger")
  })
  Customer toResponse(com.pichincha.customer.domain.Customer domain);

  /**
   * Maps UpdateCustomerRequest to domain Customer
   */
  @Mappings({
      @Mapping(target = "customerId", ignore = true),
      @Mapping(target = "personId", ignore = true),
      @Mapping(target = "createdAt", ignore = true),
      @Mapping(target = "updatedAt", source = "request", qualifiedByName = "setCurrentTime"),
      @Mapping(target = "status", ignore = true)
  })
  com.pichincha.customer.domain.Customer toDomain(UpdateCustomerRequest request);

  /**
   * Updates domain Customer from UpdateCustomerRequest
   */
  @Mappings({
      @Mapping(target = "customerId", ignore = true),
      @Mapping(target = "personId", ignore = true),
      @Mapping(target = "createdAt", ignore = true),
      @Mapping(target = "updatedAt", source = "request", qualifiedByName = "setCurrentTime"),
      @Mapping(target = "status", ignore = true)
  })
  void updateFromRequest(UpdateCustomerRequest request, @MappingTarget com.pichincha.customer.domain.Customer domain);

  /**
   * Converts string customerId to Integer for OpenAPI model
   */
  @org.mapstruct.Named("mapCustomerIdToInteger")
  default Integer mapCustomerIdToInteger(String customerId) {
    if (customerId == null || customerId.isEmpty()) {
      return null;
    }
    try {
      return customerId.startsWith(CustomerConstants.CUSTOMER_ID_PREFIX) 
          ? Integer.parseInt(customerId.substring(CustomerConstants.CUSTOMER_ID_PREFIX.length()))
          : Integer.parseInt(customerId);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  /**
   * Converts Integer customerId to String for domain model
   */
  default String mapIntegerToCustomerId(Integer customerId) {
    return customerId != null ? CustomerConstants.CUSTOMER_ID_PREFIX + customerId : null;
  }

  /**
   * Sets active status for new customers
   */
  @org.mapstruct.Named("setActiveStatus")
  default CustomerStatus setActiveStatus(Object request) {
    return CustomerStatus.ACTIVE;
  }

  /**
   * Sets current timestamp
   */
  @org.mapstruct.Named("setCurrentTime")
  default LocalDateTime setCurrentTime(Object request) {
    return LocalDateTime.now();
  }
}
