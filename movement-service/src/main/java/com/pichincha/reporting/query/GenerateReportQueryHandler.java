package com.pichincha.reporting.query;

import com.pichincha.common.infrastructure.input.adapter.rest.models.ReportResponse;
import com.pichincha.reporting.query.model.GenerateReportQuery;
import com.pichincha.reporting.service.ReportService;
import java.net.UnknownHostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GenerateReportQueryHandler {

  private final ReportService reportService;

  public Mono<ReportResponse> handle(GenerateReportQuery query) throws UnknownHostException {
    return reportService.generateReport(
        query.getCustomerId(),
        query.getStartDate(),
        query.getEndDate(),
        query.getHeaders().getXGuid(),
        query.getHeaders().getXChannel(),
        query.getHeaders().getXMedium(),
        query.getHeaders().getXApp(),
        query.getHeaders().getXSession(),
        query.getHeaders().getAuthorization()
    );
  }
}