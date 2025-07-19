package com.pichincha.customer.exception;

/**
 * Exception thrown when customer already exists
 */
public class CustomerAlreadyExistsException extends CustomerException {

  public CustomerAlreadyExistsException(String identification) {
    super(String.format("Customer already exists with identification: %s", identification), "CUSTOMER_ALREADY_EXISTS");
  }

  public CustomerAlreadyExistsException(String email, boolean isEmail) {
    super(String.format("Customer already exists with email: %s", email), "CUSTOMER_ALREADY_EXISTS");
  }
}
