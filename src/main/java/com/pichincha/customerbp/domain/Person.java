package com.pichincha.customerbp.domain;

import org.springframework.data.relational.core.mapping.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class Person {

  @NotBlank(message = "Name is mandatory")
  @Column("name")
  private String name;

  @NotBlank(message = "Gender is mandatory")
  @Column("gender")
  private String gender;

  @Min(value = 18, message = "Age must be at least 18")
  @Column("age")
  private int age;

  @Size(max = 10)
  @Pattern(regexp = "\\d{10}", message = "Identification must be 10 digits")
  @Column("identification")
  private String identification;

  @NotBlank
  @Column("address")
  private String address;

  @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits")
  @Column("phone")
  private String phone;

  @Email(message = "Invalid email format")
  @Column("email")
  private String email;
}