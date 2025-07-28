package com.pichincha.movement.infrastructure.exception;

public class MovementNotFoundException extends MovementServiceException {
  
  private static final String ERROR_CODE = "MOVEMENT_NOT_FOUND";
  
  public MovementNotFoundException(String movementId) {
    super(ERROR_CODE, "Movement not found with ID: " + movementId);
  }
  
  public MovementNotFoundException(String accountNumber, String dateRange) {
    super(ERROR_CODE, "No movements found for account: " + accountNumber + 
          " in date range: " + dateRange);
  }
}
