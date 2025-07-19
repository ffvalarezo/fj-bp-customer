package com.pichincha.customer.service.validation;

import com.pichincha.customer.exception.CustomerValidationException;
import com.pichincha.customer.util.CustomerConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Customer validation service following Clean Code principles
 * Implements Single Responsibility Principle (SRP)
 */
@Slf4j
@Component
public class CustomerValidationService {

  private static final Pattern EMAIL_PATTERN = Pattern.compile(
    "^[A-Za-z0-9][A-Za-z0-9+_.-]*[A-Za-z0-9]@[A-Za-z0-9][A-Za-z0-9.-]*[A-Za-z0-9]\\.[A-Za-z]{2,}$"
  );
  
  private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");
  private static final Pattern DOCUMENT_PATTERN = Pattern.compile("^\\d{1,10}$");

  /**
   * Validates customer creation data
   */
  public void validateCustomerCreationData(String firstName, String lastName, String email,
                                          String phoneNumber, String documentId, String password) {
    log.debug("Validating customer creation data for email: {}", email);
    
    validateBasicPersonData(firstName, lastName, email, phoneNumber, documentId);
    validatePassword(password);
  }

  /**
   * Validates basic person information
   */
  public void validateBasicPersonData(String firstName, String lastName, String email,
                                     String phoneNumber, String documentId) {
    validateRequiredField(firstName, "First name");
    validateRequiredField(lastName, "Last name");
    validateEmail(email);
    validatePhoneNumber(phoneNumber);
    validateDocumentId(documentId);
  }

  /**
   * Validates password requirements
   */
  public void validatePassword(String password) {
    validateRequiredField(password, "Password");
    
    if (password != null && password.length() < CustomerConstants.PASSWORD_MIN_LENGTH) {
      throw new CustomerValidationException(
        String.format("Password must be at least %d characters long", 
                     CustomerConstants.PASSWORD_MIN_LENGTH)
      );
    }
  }

  /**
   * Validates email format
   */
  public void validateEmail(String email) {
    validateRequiredField(email, "Email");
    
    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new CustomerValidationException("Invalid email format");
    }
  }

  /**
   * Validates phone number format
   */
  public void validatePhoneNumber(String phoneNumber) {
    validateRequiredField(phoneNumber, "Phone number");
    
    if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
      throw new CustomerValidationException("Phone number must be exactly 10 digits");
    }
  }

  /**
   * Validates document ID format
   */
  public void validateDocumentId(String documentId) {
    validateRequiredField(documentId, "Document ID");
    
    if (!DOCUMENT_PATTERN.matcher(documentId).matches()) {
      throw new CustomerValidationException("Document ID must be 1-10 digits");
    }
  }

  /**
   * Validates required field is not null or empty
   */
  private void validateRequiredField(String field, String fieldName) {
    if (field == null || field.trim().isEmpty()) {
      throw new CustomerValidationException(fieldName + " is required");
    }
  }
}
