package com.pichincha.customer.helper;

import com.pichincha.customer.domain.Customer;
import com.pichincha.customer.domain.enums.CustomerStatus;
import com.pichincha.customer.service.validation.CustomerValidationService;
import com.pichincha.customer.util.CustomerConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Factory for customer creation with validation and business rules
 * Implements Factory pattern for consistent customer object creation
 * Follows Clean Code and SOLID principles
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerFactory {

    private final CustomerValidationService validationService;

    public Customer createCustomer(String firstName, String lastName, String email,
                                   String phoneNumber, String documentId, String password) {

        log.debug("Creating customer with email: {}", email);

        validationService.validateCustomerCreationData(firstName, lastName, email, 
                                                       phoneNumber, documentId, password);

        return Customer.builder()
            .fullName(firstName + " " + lastName)
            .gender(CustomerConstants.DEFAULT_GENDER)
            .age(CustomerConstants.DEFAULT_AGE)
            .identification(documentId)
            .address(CustomerConstants.DEFAULT_ADDRESS)
            .celular(phoneNumber)
            .email(email)
            .customerId(generateCustomerId())
            .password(encodePassword(password))
            .active(true)
            .status(CustomerStatus.ACTIVE)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    private String generateCustomerId() {
        return CustomerConstants.CUSTOMER_ID_PREFIX + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String encodePassword(String password) {
        return "ENCODED_" + password;
    }
}
