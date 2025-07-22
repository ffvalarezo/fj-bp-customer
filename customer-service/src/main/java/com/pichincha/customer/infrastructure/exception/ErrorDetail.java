package com.pichincha.customer.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetail {

	private String code;
	private String message;
	private String businessMessage;
}
