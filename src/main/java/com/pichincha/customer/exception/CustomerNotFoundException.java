package com.pichincha.customer.exception;

/**
 * Exception thrown when a customer is not found
 */
public class CustomerNotFoundException extends CustomerException {

  public CustomerNotFoundException(Integer customerId) {
    super(String.format("Customer not found with ID: %d", customerId), "CUSTOMER_NOT_FOUND");
  }

  public CustomerNotFoundException(String identification) {
    super(String.format("Customer not found with identification: %s", identification), "CUSTOMER_NOT_FOUND");
  }
}
