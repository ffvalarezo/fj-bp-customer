package com.pichincha.customer.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class TransactionalLogUtil {

	private TransactionalLogUtil() {
		// Utility class
	}

	public static final String TRANSACTION_LOG_PREFIX = "Transaction Log [CUSTOMER_SERVICE]";
	public static final String CIRCUIT_BREAKER_LOG_PREFIX = "Circuit Breaker Log [CUSTOMER_SERVICE]";
	public static final String FALLBACK_LOG_PREFIX = "Fallback Log [CUSTOMER_SERVICE]";
	public static final String ERROR_LOG_PREFIX = "Error Log [CUSTOMER_SERVICE]";

	public static final String STATUS_SUCCESS = "SUCCESS";
	public static final String STATUS_ERROR = "ERROR";
	public static final String STATUS_FALLBACK = "FALLBACK";
	public static final String STATUS_TIMEOUT = "TIMEOUT";
	public static final String STATUS_CIRCUIT_OPEN = "CIRCUIT_OPEN";

	public static void logTransactionStart(String operation, String requestId, String customerId, String channel,
			String app) {
		log.info("{} - Starting {} operation. Request ID: {}, Customer ID: {}, Channel: {}, App: {}",
				TRANSACTION_LOG_PREFIX, operation, requestId, customerId, channel, app);
	}

	public static void logTransactionSuccess(String operation, String requestId, String customerId,
			String customerName) {
		log.info(
				"{} - Successfully completed {} operation. Request ID: {}, Customer ID: {}, Customer Name: {}, Status: {}",
				TRANSACTION_LOG_PREFIX, operation, requestId, customerId, customerName, STATUS_SUCCESS);
	}

	public static void logTransactionError(String operation, String requestId, String customerId, String errorType,
			String errorMessage, String channel) {
		log.error(
				"{} - Error in {} operation. Request ID: {}, Customer ID: {}, Error Type: {}, Error Message: {}, Channel: {}, Status: {}",
				TRANSACTION_LOG_PREFIX, operation, requestId, customerId, errorType, errorMessage, channel,
				STATUS_ERROR);
	}

	public static void logCircuitBreakerActivation(String operation, String requestId, String circuitState,
			String errorInfo) {
		log.warn("{} - Circuit breaker activated for {} operation. Request ID: {}, Circuit State: {}, Error: {}",
				CIRCUIT_BREAKER_LOG_PREFIX, operation, requestId, circuitState, errorInfo);
	}

	public static void logFallbackActivation(String operation, String requestId, String customerId, String reason) {
		log.warn("{} - Fallback activated for {} operation. Request ID: {}, Customer ID: {}, Reason: {}, Status: {}",
				FALLBACK_LOG_PREFIX, operation, requestId, customerId, reason, STATUS_FALLBACK);
	}
}
