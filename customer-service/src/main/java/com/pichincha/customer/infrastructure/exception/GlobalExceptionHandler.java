package com.pichincha.customer.infrastructure.exception;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.pichincha.customer.domain.CustomProblemDetail;
import com.pichincha.customer.domain.ErrorDetail;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ServerWebExchange;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private final ConfigPropertiesErrorCatalog configProperties;
	private static final String DOMAIN_HOME = "http://localhost";
	private static final String CONTEXT = "/customer";

	public GlobalExceptionHandler(ConfigPropertiesErrorCatalog configProperties) {
		this.configProperties = configProperties;
	}

	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<Object> handleCustomerNotFoundException(CustomerNotFoundException exception,
			WebRequest request) {
		Map<String, Object> data = configProperties.getValuesErrorCatalogByKey("notFoundException");
		CustomProblemDetail problemDetail = getProblemDetail(data.get("title").toString(), exception.getMessage(),
				DOMAIN_HOME, CONTEXT, data.get("resource").toString(), data.get("component").toString(),
				data.get("backend").toString());
		return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(CustomerValidationException.class)
	public ResponseEntity<Object> handleCustomerValidationException(CustomerValidationException exception,
			WebRequest request) {
		Map<String, Object> data = configProperties.getValuesErrorCatalogByKey("validationException");
		CustomProblemDetail problemDetail = getProblemDetail(data.get("title").toString(), exception.getMessage(),
				DOMAIN_HOME, CONTEXT, data.get("resource").toString(), data.get("component").toString(),
				data.get("backend").toString());
		List<ErrorDetail> allErrorDetailByKey = getAllErrorDetailByKey(data);
		problemDetail.setErrors(allErrorDetailByKey);
		return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleGenericException(Exception exception, ServerWebExchange request) {
		Map<String, Object> data = configProperties.getValuesErrorCatalogByKey("internalServerError");
		String resource = data.get("resource") != null ? data.get("resource").toString() : "unknownResource";
		String component = data.get("component") != null ? data.get("component").toString() : "unknownComponent";
		String backend = data.get("backend") != null ? data.get("backend").toString() : "unknownBackend";
		CustomProblemDetail problemDetail = getProblemDetail("Internal Server Error", exception.getMessage(),
				DOMAIN_HOME, CONTEXT, resource, component, backend);
		List<ErrorDetail> allErrorDetailByKey = getAllErrorDetailByKey(data);
		problemDetail.setErrors(allErrorDetailByKey);
		return new ResponseEntity<>(problemDetail, HttpStatus.INTERNAL_SERVER_ERROR);
	}
		
	@ExceptionHandler(WebExchangeBindException.class)
	public ResponseEntity<CustomProblemDetail> handleWebExchangeBindException(
			WebExchangeBindException exception) {
		List<ErrorDetail> errorDetails = exception.getFieldErrors().stream()
				.map(error -> ErrorDetail.builder()
						.code(error.getField())
						.message(error.getDefaultMessage())
						.businessMessage(error.getDefaultMessage())
						.build())
				.toList();
		CustomProblemDetail problemDetail = CustomProblemDetail.forStatusAndDetail(
				HttpStatus.BAD_REQUEST.value(), "Validation failed");
		problemDetail.setTitle("Validation Error");
		problemDetail.setInstance(DOMAIN_HOME);
		problemDetail.setType(CONTEXT);
		problemDetail.setResource("customer");
		problemDetail.setComponent("customerComponent");
		problemDetail.setBackend("customerBackend");
		problemDetail.setErrors(errorDetails);
		return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
	}

	private CustomProblemDetail getProblemDetail(String title, String detail, String instance, String type,
			String resource, String component, String backend) {
		CustomProblemDetail problemDetail = CustomProblemDetail.forStatusAndDetail(403, detail);
		problemDetail.setTitle(title);
		problemDetail.setInstance(instance);
		problemDetail.setType(type);
		problemDetail.setResource(resource);
		problemDetail.setComponent(component);
		problemDetail.setBackend(backend);
		return problemDetail;
	}

	private List<ErrorDetail> getAllErrorDetailByKey(ConstraintViolationException exception) {
		return exception.getConstraintViolations().stream()
				.map(violation -> ErrorDetail.builder().code(violation.getPropertyPath().toString())
						.message(violation.getMessage()).businessMessage(violation.getMessage()).build())
				.toList();
	}

	private List<ErrorDetail> getAllErrorDetailByKey(Map<String, Object> data) {
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> errorsData = (List<Map<String, Object>>) data.get("errors");
		if (errorsData == null || errorsData.isEmpty()) {
			return List.of();
		}
		return errorsData.stream()
				.map(errorMap -> ErrorDetail.builder().code((String) errorMap.get("code"))
						.message((String) errorMap.get("message"))
						.businessMessage((String) errorMap.get("businessMessage")).build())
				.collect(Collectors.toList());
	}
}