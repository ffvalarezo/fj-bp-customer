package com.pichincha.customer.infrastructure.exception;

public class CustomerValidationException extends CustomerServiceException {
  
  private static final String ERROR_CODE = "CUSTOMER_VALIDATION_ERROR";
  
  public CustomerValidationException(String message) {
    super(ERROR_CODE, message);
  }
  
  public CustomerValidationException(String message, Throwable cause) {
    super(ERROR_CODE, message, cause);
  }
}
