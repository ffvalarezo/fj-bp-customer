package com.pichincha.movement.infrastructure.exception;

public abstract class MovementServiceException extends RuntimeException {
  
  private final String errorCode;
  
  protected MovementServiceException(String errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }
  
  protected MovementServiceException(String errorCode, String message, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }
  
  public String getErrorCode() {
    return errorCode;
  }
}
