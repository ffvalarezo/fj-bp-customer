package com.pichincha.reporting.service.impl;

import com.pichincha.common.infrastructure.input.adapter.rest.models.ReportResponse;
import com.pichincha.reporting.query.GenerateReportQueryHandler;
import com.pichincha.reporting.query.model.GenerateReportQuery;
import com.pichincha.reporting.service.ReportQueryService;
import java.net.UnknownHostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReportQueryServiceImpl implements ReportQueryService {

  private final GenerateReportQueryHandler handler;

  @Override
  public Mono<ReportResponse> getReport(GenerateReportQuery query) throws UnknownHostException {
    return handler.handle(query);
  }
}