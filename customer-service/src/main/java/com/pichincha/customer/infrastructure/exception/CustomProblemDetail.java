package com.pichincha.customer.infrastructure.exception;

import java.net.URI;
import java.util.List;

import org.springframework.http.ProblemDetail;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CustomProblemDetail extends ProblemDetail {

	private static final long serialVersionUID = 1L;
	private String resource;
	private String component;
	private String backend;
	private transient List<ErrorDetail> errors;

	public static CustomProblemDetail forStatusAndDetail(int status, String detail) {
		CustomProblemDetail problemDetail = new CustomProblemDetail();
		problemDetail.setStatus(status);
		problemDetail.setDetail(detail);
		return problemDetail;
	}

	public static CustomProblemDetail forStatus(int status) {
		return forStatusAndDetail(status, null);
	}

	public void setInstance(String instance) {
		super.setInstance(URI.create(instance));
	}

	public void setType(String type) {
		if (type != null && !type.isEmpty()) {
			super.setType(URI.create(type));
		}
	}
}
