package com.pichincha.customer.exception;

/**
 * Base exception for customer domain operations
 */
public class CustomerException extends RuntimeException {

  private final String errorCode;

  public CustomerException(String message) {
    super(message);
    this.errorCode = "CUSTOMER_ERROR";
  }

  public CustomerException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  public CustomerException(String message, Throwable cause) {
    super(message, cause);
    this.errorCode = "CUSTOMER_ERROR";
  }

  public CustomerException(String message, String errorCode, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  public String getErrorCode() {
    return errorCode;
  }
}
