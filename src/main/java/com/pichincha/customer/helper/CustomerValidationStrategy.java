package com.pichincha.customer.helper;

import com.pichincha.customer.domain.Customer;

/**
 * Strategy interface for customer validation
 * Implements strategy pattern for different validation rules
 */
public interface CustomerValidationStrategy {
  
  /**
   * Validates customer data based on specific strategy
   * @param customer Customer to validate
   * @return true if valid, throws exception if invalid
   */
  boolean validate(Customer customer);
  
  /**
   * Returns the name of the validation strategy
   * @return strategy name
   */
  String getStrategyName();
}
