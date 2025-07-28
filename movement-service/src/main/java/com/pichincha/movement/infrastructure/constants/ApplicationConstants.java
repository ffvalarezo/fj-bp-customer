package com.pichincha.movement.infrastructure.constants;

/**
 * Application constants for timeout, connection, and configuration values.
 */
public final class ApplicationConstants {

  private ApplicationConstants() {
    // Utility class
  }

  // HTTP Client Timeouts (milliseconds)
  public static final int CONNECTION_TIMEOUT_MS = 10_000;
  public static final int READ_TIMEOUT_MS = 10_000;
  public static final int WRITE_TIMEOUT_MS = 10_000;
  public static final int RESPONSE_TIMEOUT_SECONDS = 10;

  // TCP Keep-Alive Settings
  public static final int TCP_KEEP_IDLE_SECONDS = 300;
  public static final int TCP_KEEP_INTERVAL_SECONDS = 60;

  // Error Messages
  public static final String CUSTOMER_ID_REQUIRED = "Customer ID cannot be null or empty";
  public static final String ACCOUNT_SERVICE_UNAVAILABLE = "Account service unavailable for customer: ";
  public static final String ACCOUNT_SERVICE_TIMEOUT = "Account service timeout for customer: ";
  public static final String ACCOUNT_SERVICE_CONNECTION_ERROR = "Unable to connect to account service for customer: ";
  public static final String REPORT_GENERATION_FAILED = "Failed to generate report for customer: ";
  public static final String MOVEMENT_DATA_COMBINATION_FAILED = "Failed to combine account and movement data";
}
