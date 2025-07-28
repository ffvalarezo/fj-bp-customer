package com.pichincha.reporting.query.model;

import com.pichincha.movement.helper.WebClientHeaderHelper;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class GenerateReportQuery {

  private final String customerId;
  private final LocalDate startDate;
  private final LocalDate endDate;
  private final WebClientHeaderHelper headers;

}
