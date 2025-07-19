package com.pichincha.customer.helper;

import com.pichincha.customer.domain.Customer;
import com.pichincha.customer.exception.CustomerValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Basic validation strategy for customer data
 * Validates essential customer information
 */
@Slf4j
@Component
public class BasicCustomerValidationStrategy implements CustomerValidationStrategy {

  @Override
  public boolean validate(Customer customer) {
    log.debug("Executing basic validation for customer: {}", customer.getCustomerId());
    
    if (customer.getFullName() == null || customer.getFullName().trim().isEmpty()) {
      throw new CustomerValidationException("Full name is required");
    }
    
    if (customer.getGender() == null || customer.getGender().trim().isEmpty()) {
      throw new CustomerValidationException("Gender is required");
    }
    
    if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
      throw new CustomerValidationException("Email is required");
    }
    
    if (!isValidEmail(customer.getEmail())) {
      throw new CustomerValidationException("Invalid email format");
    }
    
    if (customer.getCelular() == null || customer.getCelular().trim().isEmpty()) {
      throw new CustomerValidationException("Phone number is required");
    }
    
    if (customer.getIdentification() == null || customer.getIdentification().trim().isEmpty()) {
      throw new CustomerValidationException("Identification is required");
    }
    
    if (customer.getAge() == null || customer.getAge() < 18) {
      throw new CustomerValidationException("Customer must be at least 18 years old");
    }
    
    return true;
  }

  @Override
  public String getStrategyName() {
    return "BasicValidation";
  }

  private boolean isValidEmail(String email) {
    return email.contains("@") && email.contains(".");
  }
}
