package com.pichincha.customer.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for customer microservice
 * Implements centralized error handling following best practices
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(CustomerNotFoundException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleCustomerNotFound(CustomerNotFoundException ex) {
    log.warn("Customer not found: {}", ex.getMessage());
    
    ErrorResponse errorResponse = ErrorResponse.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.NOT_FOUND.value())
      .error("Not Found")
      .message(ex.getMessage())
      .errorCode(ex.getErrorCode())
      .build();
      
    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
  }

  @ExceptionHandler(CustomerValidationException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleCustomerValidation(CustomerValidationException ex) {
    log.warn("Customer validation error: {}", ex.getMessage());
    
    ErrorResponse errorResponse = ErrorResponse.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.BAD_REQUEST.value())
      .error("Bad Request")
      .message(ex.getMessage())
      .errorCode(ex.getErrorCode())
      .build();
      
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
  }

  @ExceptionHandler(CustomerAlreadyExistsException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleCustomerAlreadyExists(CustomerAlreadyExistsException ex) {
    log.warn("Customer already exists: {}", ex.getMessage());
    
    ErrorResponse errorResponse = ErrorResponse.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.CONFLICT.value())
      .error("Conflict")
      .message(ex.getMessage())
      .errorCode(ex.getErrorCode())
      .build();
      
    return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
  }

  @ExceptionHandler(CustomerException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleCustomerException(CustomerException ex) {
    log.error("Customer error: {}", ex.getMessage(), ex);
    
    ErrorResponse errorResponse = ErrorResponse.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
      .error("Internal Server Error")
      .message(ex.getMessage())
      .errorCode(ex.getErrorCode())
      .build();
      
    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
  }

  @ExceptionHandler(WebExchangeBindException.class)
  public Mono<ResponseEntity<ErrorResponse>> handleValidationErrors(WebExchangeBindException ex) {
    log.warn("Validation errors: {}", ex.getMessage());
    
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ErrorResponse errorResponse = ErrorResponse.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.BAD_REQUEST.value())
      .error("Validation Failed")
      .message("Invalid input data")
      .errorCode("VALIDATION_ERROR")
      .validationErrors(errors)
      .build();
      
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
  }

  @ExceptionHandler(Exception.class)
  public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex) {
    log.error("Unexpected error: {}", ex.getMessage(), ex);
    
    ErrorResponse errorResponse = ErrorResponse.builder()
      .timestamp(LocalDateTime.now())
      .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
      .error("Internal Server Error")
      .message("An unexpected error occurred")
      .errorCode("INTERNAL_ERROR")
      .build();
      
    return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
  }
}
