package com.pichincha.customer.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Person domain entity representing a person's basic information.
 * This entity serves as a base class for more specific person types.
 * Based on exercise requirements from fullstack exercise documentation.
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Person {

  private Long personId;

  @NotBlank(message = "Full name is required")
  @Size(max = 100, message = "Full name must not exceed 100 characters")
  private String fullName;

  @NotBlank(message = "Gender is required")
  @Size(max = 10, message = "Gender must not exceed 10 characters")
  private String gender;

  @Min(value = 18, message = "Age must be at least 18")
  private Integer age;

  @NotBlank(message = "Identification is required")
  @Pattern(regexp = "^\\d{1,10}$", message = "Identification must contain only digits and be 1-10 characters long")
  private String identification;

  @NotBlank(message = "Address is required")
  @Size(max = 200, message = "Address must not exceed 200 characters")
  private String address;

  @NotBlank(message = "Celular is required")
  @Pattern(regexp = "^\\d{10}$", message = "Celular must be exactly 10 digits")
  private String celular;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  private String email;

  /**
   * Business method to validate person age requirement
   */
  public boolean isValidAge() {
    return this.age != null && this.age >= 18;
  }

  /**
   * Business method to get formatted identification
   */
  public String getFormattedIdentification() {
    return this.identification != null ? this.identification.trim() : "";
  }

  /**
   * Business method to validate email format
   */
  public boolean hasValidEmail() {
    return this.email != null && this.email.contains("@") && this.email.contains(".");
  }
}
