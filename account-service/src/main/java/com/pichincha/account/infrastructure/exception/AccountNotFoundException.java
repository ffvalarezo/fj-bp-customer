package com.pichincha.account.infrastructure.exception;

public class AccountNotFoundException extends AccountServiceException {
  
  private static final String ERROR_CODE = "ACCOUNT_NOT_FOUND";
  
  public AccountNotFoundException(String accountNumber) {
    super(ERROR_CODE, "Account not found with number: " + accountNumber);
  }
  
  public AccountNotFoundException(String accountNumber, Throwable cause) {
    super(ERROR_CODE, "Account not found with number: " + accountNumber, cause);
  }
}
