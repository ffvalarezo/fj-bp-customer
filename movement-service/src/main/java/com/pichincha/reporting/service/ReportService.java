package com.pichincha.reporting.service;

import com.pichincha.common.infrastructure.input.adapter.rest.models.ReportResponse;
import reactor.core.publisher.Mono;

import java.net.UnknownHostException;
import java.time.LocalDate;

public interface ReportService {

	Mono<ReportResponse> generateReport(String customerId, LocalDate start, LocalDate end, String xGuid,
			String xChannel, String xMedium, String xApp, String xSession, String token) throws UnknownHostException;
}
