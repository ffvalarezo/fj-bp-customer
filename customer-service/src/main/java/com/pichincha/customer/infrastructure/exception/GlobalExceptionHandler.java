package com.pichincha.customer.infrastructure.exception;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.pichincha.common.exception.LogException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@Autowired
	private ConfigPropertiesErrorCatalog configProperties;

	private static final String DOMAIN_HOME = "http://localhost";
	private static final String CONTEXT = "/customer";

	@ExceptionHandler(LogException.class)
	public ResponseEntity<Object> handleForbiddenException(LogException ex, WebRequest request) {
		Map<String, Object> data = configProperties.getValuesErrorCatalogByKey("newException");

		CustomProblemDetail problemDetail = getProblemDetail(data.get("title").toString(), ex.getMessage(), DOMAIN_HOME,
				CONTEXT, data.get("resource").toString(), data.get("component").toString(),
				data.get("backend").toString());

		List<ErrorDetail> allErrorDetailByKey = getAllErrorDetailByKey(data);
		problemDetail.setErrors(allErrorDetailByKey);

		return new ResponseEntity<>(problemDetail, HttpStatus.FORBIDDEN);
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