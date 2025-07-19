package com.pichincha.customer.exception;

/**
 * Exception thrown when customer data is invalid
 */
public class CustomerValidationException extends CustomerException {

  public CustomerValidationException(String message) {
    super(message, "CUSTOMER_VALIDATION_ERROR");
  }

  public CustomerValidationException(String field, String value) {
    super(String.format("Invalid value '%s' for field '%s'", value, field), "CUSTOMER_VALIDATION_ERROR");
  }
}
