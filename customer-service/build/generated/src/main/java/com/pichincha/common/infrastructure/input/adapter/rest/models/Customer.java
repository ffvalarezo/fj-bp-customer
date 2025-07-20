package com.pichincha.common.infrastructure.input.adapter.rest.models;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * Customer
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-20T15:52:36.912883200-05:00[America/Guayaquil]", comments = "Generator version: 7.9.0")
public class Customer implements Serializable {

  private static final long serialVersionUID = 1L;

  private String fullName;

  private String gender;

  private Integer age;

  private String identification;

  private String address;

  private String celular;

  private String email;

  private Integer customerId;

  private String password;

  private Boolean active;

  public Customer() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public Customer(String fullName, String gender, Integer age, String identification, String address, String celular, String email, String password, Boolean active) {
    this.fullName = fullName;
    this.gender = gender;
    this.age = age;
    this.identification = identification;
    this.address = address;
    this.celular = celular;
    this.email = email;
    this.password = password;
    this.active = active;
  }

  public Customer fullName(String fullName) {
    this.fullName = fullName;
    return this;
  }

  /**
   * Get fullName
   * @return fullName
   */
  @NotNull 
  @Schema(name = "fullName", example = "Jose Lema", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("fullName")
  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public Customer gender(String gender) {
    this.gender = gender;
    return this;
  }

  /**
   * Get gender
   * @return gender
   */
  @NotNull 
  @Schema(name = "gender", example = "Male", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("gender")
  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public Customer age(Integer age) {
    this.age = age;
    return this;
  }

  /**
   * Get age
   * minimum: 18
   * @return age
   */
  @NotNull @Min(18) 
  @Schema(name = "age", example = "35", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("age")
  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
  }

  public Customer identification(String identification) {
    this.identification = identification;
    return this;
  }

  /**
   * Get identification
   * @return identification
   */
  @NotNull @Pattern(regexp = "^\\d{1,10}$") 
  @Schema(name = "identification", example = "1234567890", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("identification")
  public String getIdentification() {
    return identification;
  }

  public void setIdentification(String identification) {
    this.identification = identification;
  }

  public Customer address(String address) {
    this.address = address;
    return this;
  }

  /**
   * Get address
   * @return address
   */
  @NotNull 
  @Schema(name = "address", example = "Otavalo st and principal", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("address")
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Customer celular(String celular) {
    this.celular = celular;
    return this;
  }

  /**
   * Get celular
   * @return celular
   */
  @NotNull @Pattern(regexp = "^\\d{10}$") 
  @Schema(name = "celular", example = "0982547852", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("celular")
  public String getCelular() {
    return celular;
  }

  public void setCelular(String celular) {
    this.celular = celular;
  }

  public Customer email(String email) {
    this.email = email;
    return this;
  }

  /**
   * Get email
   * @return email
   */
  @NotNull @jakarta.validation.constraints.Email 
  @Schema(name = "email", example = "jose.lema@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("email")
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Customer customerId(Integer customerId) {
    this.customerId = customerId;
    return this;
  }

  /**
   * Get customerId
   * @return customerId
   */
  
  @Schema(name = "customerId", accessMode = Schema.AccessMode.READ_ONLY, example = "1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("customerId")
  public Integer getCustomerId() {
    return customerId;
  }

  public void setCustomerId(Integer customerId) {
    this.customerId = customerId;
  }

  public Customer password(String password) {
    this.password = password;
    return this;
  }

  /**
   * Get password
   * @return password
   */
  @NotNull 
  @Schema(name = "password", example = "1234", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("password")
  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Customer active(Boolean active) {
    this.active = active;
    return this;
  }

  /**
   * Get active
   * @return active
   */
  @NotNull 
  @Schema(name = "active", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("active")
  public Boolean getActive() {
    return active;
  }

  public void setActive(Boolean active) {
    this.active = active;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Customer customer = (Customer) o;
    return Objects.equals(this.fullName, customer.fullName) &&
        Objects.equals(this.gender, customer.gender) &&
        Objects.equals(this.age, customer.age) &&
        Objects.equals(this.identification, customer.identification) &&
        Objects.equals(this.address, customer.address) &&
        Objects.equals(this.celular, customer.celular) &&
        Objects.equals(this.email, customer.email) &&
        Objects.equals(this.customerId, customer.customerId) &&
        Objects.equals(this.password, customer.password) &&
        Objects.equals(this.active, customer.active);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fullName, gender, age, identification, address, celular, email, customerId, password, active);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Customer {\n");
    sb.append("    fullName: ").append(toIndentedString(fullName)).append("\n");
    sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
    sb.append("    age: ").append(toIndentedString(age)).append("\n");
    sb.append("    identification: ").append(toIndentedString(identification)).append("\n");
    sb.append("    address: ").append(toIndentedString(address)).append("\n");
    sb.append("    celular: ").append(toIndentedString(celular)).append("\n");
    sb.append("    email: ").append(toIndentedString(email)).append("\n");
    sb.append("    customerId: ").append(toIndentedString(customerId)).append("\n");
    sb.append("    password: ").append(toIndentedString(password)).append("\n");
    sb.append("    active: ").append(toIndentedString(active)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

