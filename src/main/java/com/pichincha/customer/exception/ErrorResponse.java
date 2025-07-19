package com.pichincha.customer.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response for API exceptions
 * Provides consistent error structure across all endpoints
 */
@Data
@Builder
public class ErrorResponse {
  
  private LocalDateTime timestamp;
  private int status;
  private String error;
  private String message;
  private String errorCode;
  private Map<String, String> validationErrors;
}
