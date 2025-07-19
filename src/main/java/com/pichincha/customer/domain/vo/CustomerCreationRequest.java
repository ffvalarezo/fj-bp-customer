package com.pichincha.customer.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * Customer creation request value object
 * Implements immutable data transfer following DDD principles
 */
@Data
@Builder
public class CustomerCreationRequest {
  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private String documentId;
  private String password;
}
