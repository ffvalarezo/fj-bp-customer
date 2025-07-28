package com.pichincha.reporting.service;

import com.pichincha.common.infrastructure.input.adapter.rest.models.ReportResponse;
import com.pichincha.reporting.query.model.GenerateReportQuery;
import java.net.UnknownHostException;
import reactor.core.publisher.Mono;

public interface ReportQueryService {

  Mono<ReportResponse> getReport(GenerateReportQuery query) throws UnknownHostException;

}