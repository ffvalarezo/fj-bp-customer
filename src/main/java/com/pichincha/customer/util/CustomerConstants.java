package com.pichincha.customer.util;

/**
 * Constants for Customer domain
 * Following Clean Code principles - centralized constants
 */
public final class CustomerConstants {

  private CustomerConstants() {
    // Utility class - prevent instantiation
  }

  // Customer ID constants
  public static final String CUSTOMER_ID_PREFIX = "CUST-";
  public static final String CUSTOMER_ID_FORMAT = "%06d";
  
  // Message constants
  public static final String CUSTOMER_NOT_FOUND_MSG = "Customer not found with ID: ";
  public static final String CUSTOMER_CREATED_MSG = "Customer created successfully with ID: {}";
  public static final String CUSTOMER_UPDATED_MSG = "Customer updated successfully: {}";
  public static final String CUSTOMER_DELETED_MSG = "Customer deleted successfully: {}";
  public static final String CUSTOMER_ACTIVATED_MSG = "Customer activated successfully: {}";
  public static final String CUSTOMER_DEACTIVATED_MSG = "Customer deactivated successfully: {}";
  
  // Default values
  public static final String DEFAULT_GENDER = "M";
  public static final Integer DEFAULT_AGE = 25;
  public static final String DEFAULT_ADDRESS = "Default Address";
  
  // Validation constants
  public static final int PASSWORD_MIN_LENGTH = 8;
  public static final int RANDOM_ID_BOUND = 100000;
}
