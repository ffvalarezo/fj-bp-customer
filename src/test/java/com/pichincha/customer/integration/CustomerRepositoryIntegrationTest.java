package com.pichincha.customer.integration;

import com.pichincha.customer.domain.Customer;
import com.pichincha.customer.domain.enums.CustomerStatus;
import com.pichincha.customer.repository.CustomerRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for CustomerRepository
 * Tests repository operations and reactive interactions
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Customer Repository Integration Tests")
class CustomerRepositoryIntegrationTest {

  @Autowired
  private CustomerRepository customerRepository;

  @Test
  @DisplayName("Should save and find customer by ID")
  void shouldSaveAndFindCustomerById() {
    // Arrange
    Customer customer = createTestCustomer("CUST-IT-001", "Integration Test");

    // Act
    Customer savedCustomer = customerRepository.save(customer).block();
    Customer foundCustomer = customerRepository.findByCustomerId(savedCustomer.getCustomerId()).block();

    // Assert
    assertThat(savedCustomer).isNotNull();
    assertThat(savedCustomer.getCustomerId()).isNotNull();
    assertThat(foundCustomer).isNotNull();
    assertThat(foundCustomer.getFullName()).isEqualTo("Integration Test");
  }

  @Test
  @DisplayName("Should find customer by email")
  void shouldFindCustomerByEmail() {
    // Arrange
    Customer customer = createTestCustomer("CUST-IT-002", "Email Test");
    String testEmail = "email.test@example.com";
    customer.setEmail(testEmail);

    // Act
    customerRepository.save(customer).block();
    Customer foundCustomer = customerRepository.findByEmail(testEmail).block();

    // Assert
    assertThat(foundCustomer).isNotNull();
    assertThat(foundCustomer.getEmail()).isEqualTo(testEmail);
  }

  @Test
  @DisplayName("Should check if customer exists by ID")
  void shouldCheckIfCustomerExistsById() {
    // Arrange
    Customer customer = createTestCustomer("CUST-IT-003", "Exists Test");
    customerRepository.save(customer).block();

    // Act
    Boolean exists = customerRepository.existsByCustomerId(customer.getCustomerId()).block();
    Boolean notExists = customerRepository.existsByCustomerId("CUST-NOT-EXISTS").block();

    // Assert
    assertThat(exists).isTrue();
    assertThat(notExists).isFalse();
  }

  @Test
  @DisplayName("Should delete customer by ID")
  void shouldDeleteCustomerById() {
    // Arrange
    Customer customer = createTestCustomer("CUST-IT-004", "Delete Test");
    Customer savedCustomer = customerRepository.save(customer).block();

    // Act
    customerRepository.deleteById(savedCustomer.getPersonId()).block();
    Customer foundCustomer = customerRepository.findByCustomerId(savedCustomer.getCustomerId()).block();

    // Assert
    assertThat(foundCustomer).isNull();
  }

  private Customer createTestCustomer(String customerId, String fullName) {
    Customer customer = new Customer();
    customer.setCustomerId(customerId);
    customer.setFullName(fullName);
    customer.setGender("M");
    customer.setAge(30);
    customer.setIdentification("1234567890");
    customer.setAddress("123 Test St");
    customer.setCelular("0987654321");
    customer.setEmail("test@example.com");
    customer.setPassword("ENCODED_password123");
    customer.setActive(true);
    customer.setStatus(CustomerStatus.ACTIVE);
    customer.setCreatedAt(LocalDateTime.now());
    customer.setUpdatedAt(LocalDateTime.now());
    return customer;
  }
}
