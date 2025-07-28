package com.pichincha.customer.infrastructure.exception;

public abstract class CustomerServiceException extends RuntimeException {
  
  private final String errorCode;
  
  protected CustomerServiceException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
  
  protected CustomerServiceException(String errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }
  
  public String getErrorCode() {
    return errorCode;
  }
}
