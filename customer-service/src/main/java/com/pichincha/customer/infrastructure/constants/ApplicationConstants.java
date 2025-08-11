package com.pichincha.customer.infrastructure.constants;

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
    public static final String ACCOUNT_SERVICE_UNAVAILABLE = "Account service unavailable for customer: ";
    public static final String ACCOUNT_SERVICE_TIMEOUT = "Account service timeout for customer: ";
    public static final String ACCOUNT_SERVICE_CONNECTION_ERROR = "Unable to connect to account service for customer: ";

}
