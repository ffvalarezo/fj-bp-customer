package com.pichincha.customer.domain;

import com.pichincha.customer.domain.enums.CustomerStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * Customer domain entity extending Person with customer-specific information.
 * This entity represents a customer in the banking system.
 * Based on fullstack exercise requirements.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Customer extends Person {

  private String customerId;

  @NotBlank(message = "Password is required")
  @Size(min = 4, max = 50, message = "Password must be between 4 and 50 characters")
  private String password;

  @NotNull(message = "Active status is required")
  private Boolean active;

  private CustomerStatus status;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  /**
   * Constructor that creates a Customer with basic person information
   */
  public Customer(String fullName, String gender, Integer age, String identification,
                  String address, String celular, String email, String customerId,
                  String password, Boolean active, CustomerStatus status) {
    super();
    this.setFullName(fullName);
    this.setGender(gender);
    this.setAge(age);
    this.setIdentification(identification);
    this.setAddress(address);
    this.setCelular(celular);
    this.setEmail(email);
    this.customerId = customerId;
    this.password = password;
    this.active = active;
    this.status = status;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Business method to activate customer
   */
  public void activateCustomer() {
    this.active = true;
    this.status = CustomerStatus.ACTIVE;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Business method to deactivate customer
   */
  public void deactivateCustomer() {
    this.active = false;
    this.status = CustomerStatus.INACTIVE;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Business method to suspend customer
   */
  public void suspendCustomer() {
    this.active = false;
    this.status = CustomerStatus.SUSPENDED;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Business method to block customer
   */
  public void blockCustomer() {
    this.active = false;
    this.status = CustomerStatus.BLOCKED;
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Business method to check if customer is active
   */
  public boolean isActiveCustomer() {
    return Boolean.TRUE.equals(this.active);
  }

  /**
   * Business method to update customer password
   */
  public void updatePassword(String newPassword) {
    if (newPassword != null && newPassword.length() >= 4) {
      this.password = newPassword;
      this.updatedAt = LocalDateTime.now();
    }
  }

  /**
   * Business method to update customer information
   */
  public void updateCustomerInfo(String fullName, String gender, Integer age, 
                                String address, String celular, String email) {
    if (fullName != null) this.setFullName(fullName);
    if (gender != null) this.setGender(gender);
    if (age != null) this.setAge(age);
    if (address != null) this.setAddress(address);
    if (celular != null) this.setCelular(celular);
    if (email != null) this.setEmail(email);
    this.updatedAt = LocalDateTime.now();
  }

  /**
   * Business method to check if customer can perform transactions
   */
  public boolean canPerformTransactions() {
    return this.isActiveCustomer() && this.isValidAge();
  }

  /**
   * Business method to get customer display name
   */
  public String getDisplayName() {
    return String.format("Customer %s - %s", this.customerId, this.getFullName());
  }
}
