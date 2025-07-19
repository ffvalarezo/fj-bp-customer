package com.pichincha.customer.helper;

import com.pichincha.customer.domain.Customer;
import com.pichincha.customer.exception.CustomerValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Enhanced validation strategy for customer data
 * Performs more strict validations for business rules
 */
@Slf4j
@Component
public class EnhancedCustomerValidationStrategy implements CustomerValidationStrategy {

  @Override
  public boolean validate(Customer customer) {
    log.debug("Executing enhanced validation for customer: {}", customer.getCustomerId());
    
    validateBasicData(customer);
    validateBusinessRules(customer);
    validateSecurityRules(customer);
    
    return true;
  }

  @Override
  public String getStrategyName() {
    return "EnhancedValidation";
  }

  private void validateBasicData(Customer customer) {
    if (customer.getFullName() == null || customer.getFullName().trim().length() < 5) {
      throw new CustomerValidationException("Full name must be at least 5 characters long");
    }
    
    if (customer.getEmail() == null || !isValidEmailFormat(customer.getEmail())) {
      throw new CustomerValidationException("Email must have valid format (domain must be valid)");
    }
    
    if (customer.getCelular() == null || !customer.getCelular().matches("^\\d{10}$")) {
      throw new CustomerValidationException("Phone number must be exactly 10 digits");
    }
  }

  private void validateBusinessRules(Customer customer) {
    if (customer.getAge() == null || customer.getAge() < 18 || customer.getAge() > 120) {
      throw new CustomerValidationException("Customer age must be between 18 and 120 years");
    }
    
    if (customer.getIdentification() == null || !customer.getIdentification().matches("^\\d{1,10}$")) {
      throw new CustomerValidationException("Identification must contain only digits (1-10 characters)");
    }
    
    if (customer.getAddress() == null || customer.getAddress().trim().length() < 10) {
      throw new CustomerValidationException("Address must be at least 10 characters long");
    }
  }

  private void validateSecurityRules(Customer customer) {
    if (customer.getPassword() == null || customer.getPassword().length() < 8) {
      throw new CustomerValidationException("Password must be at least 8 characters long");
    }
    
    if (!hasUpperCase(customer.getPassword())) {
      throw new CustomerValidationException("Password must contain at least one uppercase letter");
    }
    
    if (!hasLowerCase(customer.getPassword())) {
      throw new CustomerValidationException("Password must contain at least one lowercase letter");
    }
    
    if (!hasDigit(customer.getPassword())) {
      throw new CustomerValidationException("Password must contain at least one digit");
    }
  }

  private boolean isValidEmailFormat(String email) {
    return email.contains("@") && email.contains(".") && 
           email.indexOf("@") < email.lastIndexOf(".") &&
           email.endsWith(".com") || email.endsWith(".ec") || email.endsWith(".org");
  }

  private boolean hasUpperCase(String password) {
    return password.chars().anyMatch(Character::isUpperCase);
  }

  private boolean hasLowerCase(String password) {
    return password.chars().anyMatch(Character::isLowerCase);
  }

  private boolean hasDigit(String password) {
    return password.chars().anyMatch(Character::isDigit);
  }
}
