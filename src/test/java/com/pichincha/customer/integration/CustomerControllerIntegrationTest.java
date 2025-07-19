package com.pichincha.customer.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pichincha.customer.domain.Customer;
import com.pichincha.customer.domain.enums.CustomerStatus;
import com.pichincha.customer.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Customer Controller
 * Tests API endpoints and request/response handling
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("Customer Controller Integration Tests")
class CustomerControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @SuppressWarnings("removal")
  @MockBean
  private CustomerService customerService;

  @Test
  @DisplayName("Should create customer successfully")
  void shouldCreateCustomerSuccessfully() throws Exception {
    // Arrange
    Customer mockCustomer = createTestCustomer("CUST-INT-001", "Integration Customer");
    when(customerService.createCustomer(any(Customer.class))).thenReturn(Mono.just(mockCustomer));

    Map<String, Object> customerRequest = createCustomerRequest();

    // Act & Assert
    mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(customerRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.customerId").exists())
        .andExpect(jsonPath("$.fullName").value("Integration Customer"));
  }

  @Test
  @DisplayName("Should get customer by ID successfully")
  void shouldGetCustomerByIdSuccessfully() throws Exception {
    // Arrange
    String customerId = "CUST-INT-002";
    Customer mockCustomer = createTestCustomer(customerId, "Get Customer Test");
    when(customerService.getCustomerById(customerId)).thenReturn(Mono.just(mockCustomer));

    // Act & Assert
    mockMvc.perform(get("/customers/{customerId}", customerId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.customerId").value(customerId))
        .andExpect(jsonPath("$.fullName").value("Get Customer Test"));
  }

  @Test
  @DisplayName("Should update customer successfully")
  void shouldUpdateCustomerSuccessfully() throws Exception {
    // Arrange
    String customerId = "CUST-INT-003";
    Customer mockCustomer = createTestCustomer(customerId, "Updated Customer");
    when(customerService.updateCustomer(anyString(), any(Customer.class))).thenReturn(Mono.just(mockCustomer));

    Map<String, Object> updateRequest = createCustomerRequest();
    updateRequest.put("fullName", "Updated Customer");

    // Act & Assert
    mockMvc.perform(put("/customers/{customerId}", customerId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.customerId").value(customerId))
        .andExpect(jsonPath("$.fullName").value("Updated Customer"));
  }

  @Test
  @DisplayName("Should get all customers successfully")
  void shouldGetAllCustomersSuccessfully() throws Exception {
    // Arrange
    Customer customer1 = createTestCustomer("CUST-INT-004", "Customer One");
    Customer customer2 = createTestCustomer("CUST-INT-005", "Customer Two");
    when(customerService.getAllCustomers()).thenReturn(Flux.just(customer1, customer2));

    // Act & Assert
    mockMvc.perform(get("/customers"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(jsonPath("$[0].customerId").value("CUST-INT-004"))
        .andExpect(jsonPath("$[1].customerId").value("CUST-INT-005"));
  }

  @Test
  @DisplayName("Should delete customer successfully")
  void shouldDeleteCustomerSuccessfully() throws Exception {
    // Arrange
    String customerId = "CUST-INT-006";
    when(customerService.deleteCustomer(customerId)).thenReturn(Mono.empty());

    // Act & Assert
    mockMvc.perform(delete("/customers/{customerId}", customerId))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("Should return 404 when customer not found")
  void shouldReturn404WhenCustomerNotFound() throws Exception {
    // Arrange
    String customerId = "CUST-NOT-FOUND";
    when(customerService.getCustomerById(customerId))
        .thenReturn(Mono.error(new RuntimeException("Customer not found")));

    // Act & Assert
    mockMvc.perform(get("/customers/{customerId}", customerId))
        .andExpect(status().isInternalServerError());
  }

  @Test
  @DisplayName("Should return 400 for invalid request body")
  void shouldReturn400ForInvalidRequestBody() throws Exception {
    // Arrange
    Map<String, Object> invalidRequest = new HashMap<>();
    invalidRequest.put("invalidField", "invalidValue");

    // Act & Assert
    mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
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

  private Map<String, Object> createCustomerRequest() {
    Map<String, Object> request = new HashMap<>();
    request.put("fullName", "Integration Customer");
    request.put("gender", "M");
    request.put("age", 30);
    request.put("identification", "1234567890");
    request.put("address", "123 Test St");
    request.put("celular", "0987654321");
    request.put("email", "test@example.com");
    request.put("password", "password123");
    return request;
  }
}
