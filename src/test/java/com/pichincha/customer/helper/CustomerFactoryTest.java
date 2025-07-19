package com.pichincha.customer.helper;

import com.pichincha.customer.domain.Customer;
import com.pichincha.customer.domain.enums.CustomerStatus;
import com.pichincha.customer.helper.CustomerFactory;
import com.pichincha.customer.service.validation.CustomerValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomerFactory following Factory Pattern best practices
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Factory Tests")
class CustomerFactoryTest {

  @Mock
  private CustomerValidationService validationService;

  @InjectMocks
  private CustomerFactory customerFactory;

  @BeforeEach
  void setUp() {
    // Mock validation to pass by default
    doNothing().when(validationService).validateCustomerCreationData(
      anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
    );
  }

  @Test
  @DisplayName("Should create customer with all required fields")
  void shouldCreateCustomerWithAllRequiredFields() {
    // Arrange
    String firstName = "John";
    String lastName = "Doe";
    String email = "john.doe@example.com";
    String phoneNumber = "0987654321";
    String documentId = "1234567890";
    String password = "password123";

    // Act
    Customer result = customerFactory.createCustomer(
      firstName, lastName, email, phoneNumber, documentId, password
    );

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getFullName()).isEqualTo("John Doe");
    assertThat(result.getEmail()).isEqualTo(email);
    assertThat(result.getCelular()).isEqualTo(phoneNumber);
    assertThat(result.getIdentification()).isEqualTo(documentId);
    assertThat(result.getPassword()).isEqualTo("ENCODED_password123");
    assertThat(result.getActive()).isTrue();
    assertThat(result.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
    assertThat(result.getCustomerId()).isNotNull();
    assertThat(result.getCustomerId()).startsWith("CUST-");
    assertThat(result.getCreatedAt()).isNotNull();
    assertThat(result.getUpdatedAt()).isNotNull();

    // Verify validation was called
    verify(validationService).validateCustomerCreationData(
      firstName, lastName, email, phoneNumber, documentId, password
    );
  }

  @Test
  @DisplayName("Should set default values for customer")
  void shouldSetDefaultValuesForCustomer() {
    // Arrange
    String firstName = "Jane";
    String lastName = "Smith";
    String email = "jane.smith@example.com";
    String phoneNumber = "1234567890";
    String documentId = "0987654321";
    String password = "password123";

    // Act
    Customer result = customerFactory.createCustomer(
      firstName, lastName, email, phoneNumber, documentId, password
    );

    // Assert
    assertThat(result.getGender()).isEqualTo("M"); // Default gender
    assertThat(result.getAge()).isEqualTo(25); // Default age
    assertThat(result.getAddress()).isEqualTo("Default Address"); // Default address
  }

  @Test
  @DisplayName("Should generate unique customer IDs")
  void shouldGenerateUniqueCustomerIds() {
    // Arrange
    String firstName = "Test";
    String lastName = "User";
    String email = "test@example.com";
    String phoneNumber = "1111111111";
    String documentId = "1111111111";
    String password = "password123";

    // Act
    Customer customer1 = customerFactory.createCustomer(
      firstName, lastName, email, phoneNumber, documentId, password
    );
    Customer customer2 = customerFactory.createCustomer(
      firstName, lastName, email, phoneNumber, documentId, password
    );

    // Assert
    assertThat(customer1.getCustomerId()).isNotEqualTo(customer2.getCustomerId());
    assertThat(customer1.getCustomerId()).startsWith("CUST-");
    assertThat(customer2.getCustomerId()).startsWith("CUST-");
  }
}
