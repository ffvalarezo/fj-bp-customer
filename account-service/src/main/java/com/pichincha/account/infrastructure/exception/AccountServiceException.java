package com.pichincha.account.infrastructure.exception;

public abstract class AccountServiceException extends RuntimeException {
  
  private final String errorCode;
  
  protected AccountServiceException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
  
  protected AccountServiceException(String errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }
  
  public String getErrorCode() {
    return errorCode;
  }
}
