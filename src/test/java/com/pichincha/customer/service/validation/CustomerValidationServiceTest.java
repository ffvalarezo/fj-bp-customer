package com.pichincha.customer.service.validation;

import com.pichincha.customer.exception.CustomerValidationException;
import com.pichincha.customer.service.validation.CustomerValidationService;
import com.pichincha.customer.util.CustomerConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Comprehensive unit tests for CustomerValidationService
 * Follows industry best practices: Parameterized tests, Edge cases, Boundary conditions
 */
@DisplayName("Customer Validation Service Tests")
class CustomerValidationServiceTest {

  private CustomerValidationService validationService;

  @BeforeEach
  void setUp() {
    validationService = new CustomerValidationService();
  }

  @Nested
  @DisplayName("Email Validation Tests")
  class EmailValidationTests {

    @ParameterizedTest
    @DisplayName("Should accept valid email formats")
    @ValueSource(strings = {
      "test@example.com",
      "user.name@domain.co.uk",
      "user+tag@example.org",
      "firstname.lastname@subdomain.example.com"
    })
    void shouldAcceptValidEmailFormats(String email) {
      assertDoesNotThrow(() -> validationService.validateEmail(email));
    }

    @ParameterizedTest
    @DisplayName("Should reject invalid email formats")
    @ValueSource(strings = {
      "invalid-email",
      "@domain.com",
      "user@",
      "user.domain.com",
      "user@domain",
      ".user@domain.com",
      "user.@domain.com"
    })
    void shouldRejectInvalidEmailFormats(String email) {
      CustomerValidationException exception = assertThrows(
        CustomerValidationException.class,
        () -> validationService.validateEmail(email)
      );
      assertThat(exception.getMessage()).contains("Invalid email format");
    }

    @ParameterizedTest
    @DisplayName("Should reject null or empty email")
    @NullSource
    @EmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n"})
    void shouldRejectNullOrEmptyEmail(String email) {
      CustomerValidationException exception = assertThrows(
        CustomerValidationException.class,
        () -> validationService.validateEmail(email)
      );
      assertThat(exception.getMessage()).contains("Email is required");
    }
  }

  @Nested
  @DisplayName("Phone Number Validation Tests")
  class PhoneNumberValidationTests {

    @ParameterizedTest
    @DisplayName("Should accept valid phone numbers")
    @ValueSource(strings = {"0987654321", "1234567890", "0000000000", "9999999999"})
    void shouldAcceptValidPhoneNumbers(String phoneNumber) {
      assertDoesNotThrow(() -> validationService.validatePhoneNumber(phoneNumber));
    }

    @ParameterizedTest
    @DisplayName("Should reject invalid phone numbers")
    @ValueSource(strings = {
      "123456789",  // 9 digits
      "12345678901", // 11 digits
      "123-456-7890", // with dashes
      "(123)456-7890", // with parentheses
      "abc1234567", // with letters
      "123 456 7890" // with spaces
    })
    void shouldRejectInvalidPhoneNumbers(String phoneNumber) {
      CustomerValidationException exception = assertThrows(
        CustomerValidationException.class,
        () -> validationService.validatePhoneNumber(phoneNumber)
      );
      assertThat(exception.getMessage()).contains("Phone number must be exactly 10 digits");
    }
  }

  @Nested
  @DisplayName("Document ID Validation Tests")
  class DocumentIdValidationTests {

    @ParameterizedTest
    @DisplayName("Should accept valid document IDs")
    @ValueSource(strings = {"1", "12", "123", "1234567890", "0123456789"})
    void shouldAcceptValidDocumentIds(String documentId) {
      assertDoesNotThrow(() -> validationService.validateDocumentId(documentId));
    }

    @ParameterizedTest
    @DisplayName("Should reject invalid document IDs")
    @ValueSource(strings = {
      "12345678901", // 11 digits
      "abc123", // with letters
      "123-456", // with dash
      "123 456" // with space
    })
    void shouldRejectInvalidDocumentIds(String documentId) {
      CustomerValidationException exception = assertThrows(
        CustomerValidationException.class,
        () -> validationService.validateDocumentId(documentId)
      );
      assertThat(exception.getMessage()).contains("Document ID must be 1-10 digits");
    }
  }

  @Nested
  @DisplayName("Password Validation Tests")
  class PasswordValidationTests {

    @ParameterizedTest
    @DisplayName("Should accept valid passwords")
    @ValueSource(strings = {"password123", "12345678", "verylongpassword123"})
    void shouldAcceptValidPasswords(String password) {
      assertDoesNotThrow(() -> validationService.validatePassword(password));
    }

    @ParameterizedTest
    @DisplayName("Should reject short passwords")
    @ValueSource(strings = {"", "1", "12", "123", "1234", "12345", "123456", "1234567"})
    void shouldRejectShortPasswords(String password) {
      CustomerValidationException exception = assertThrows(
        CustomerValidationException.class,
        () -> validationService.validatePassword(password)
      );
      assertThat(exception.getMessage()).contains(
        String.format("Password must be at least %d characters long", CustomerConstants.PASSWORD_MIN_LENGTH)
      );
    }
  }

  @Nested
  @DisplayName("Customer Creation Data Validation Tests")
  class CustomerCreationDataValidationTests {

    @Test
    @DisplayName("Should validate complete customer creation data successfully")
    void shouldValidateCompleteCustomerCreationDataSuccessfully() {
      assertDoesNotThrow(() -> validationService.validateCustomerCreationData(
        "John",
        "Doe",
        "john.doe@example.com",
        "0987654321",
        "1234567890",
        "password123"
      ));
    }

    @Test
    @DisplayName("Should reject customer creation data with invalid email")
    void shouldRejectCustomerCreationDataWithInvalidEmail() {
      CustomerValidationException exception = assertThrows(
        CustomerValidationException.class,
        () -> validationService.validateCustomerCreationData(
          "John",
          "Doe",
          "invalid-email",
          "0987654321",
          "1234567890",
          "password123"
        )
      );
      assertThat(exception.getMessage()).contains("Invalid email format");
    }

    @Test
    @DisplayName("Should reject customer creation data with short password")
    void shouldRejectCustomerCreationDataWithShortPassword() {
      CustomerValidationException exception = assertThrows(
        CustomerValidationException.class,
        () -> validationService.validateCustomerCreationData(
          "John",
          "Doe",
          "john.doe@example.com",
          "0987654321",
          "1234567890",
          "short"
        )
      );
      assertThat(exception.getMessage()).contains("Password must be at least");
    }
  }
}
