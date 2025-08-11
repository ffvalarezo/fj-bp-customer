package com.pichincha.reporting.service.impl;

import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;
import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementResponse;
import com.pichincha.common.infrastructure.input.adapter.rest.models.ReportResponse;
import com.pichincha.common.infrastructure.input.adapter.rest.models.ReportResponseSummary;
import com.pichincha.movement.helper.WebClientHeaderHelper;
import com.pichincha.movement.infrastructure.exception.ExternalServiceException;
import com.pichincha.movement.service.MovementQueryService;
import com.pichincha.reporting.repository.AccountClientRepository;
import com.pichincha.reporting.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static com.pichincha.movement.infrastructure.constants.ApplicationConstants.*;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final MovementQueryService movementQueryService;
    private final AccountClientRepository accountClientRepository;


    public ReportServiceImpl(AccountClientRepository accountClientRepository,
                             MovementQueryService movementQueryService) {
        this.accountClientRepository = accountClientRepository;
        this.movementQueryService = movementQueryService;
    }

    @Override
    public Mono<ReportResponse> generateReport(String customerId, LocalDate start, LocalDate end, String xGuid,
                                               String xChannel, String xMedium, String xApp, String xSession, String token) {
        if (customerId == null || customerId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException(CUSTOMER_ID_REQUIRED));
        }

        WebClientHeaderHelper headers = new WebClientHeaderHelper(xGuid, xChannel, xMedium, xApp, xSession, token);

        return accountClientRepository.getAccountsByCustomerId(customerId, headers)
                .doOnNext(accounts -> log.debug("Found {} accounts for customer {}", accounts.size(), customerId))
                .flatMap(accounts -> combineAccountsWithMovements(accounts, start, end))
                .doOnSuccess(report -> log.info("Report generated successfully for customer {}", customerId))
                .doOnError(error -> log.error("Error generating report for customer {}: {}", customerId,
                        error.getMessage()))
                .onErrorMap(throwable -> {
                    if (throwable instanceof ExternalServiceException) {
                        return throwable;
                    }
                    return new ExternalServiceException(REPORT_GENERATION_FAILED + customerId, throwable);
                });
    }

    @SuppressWarnings("unchecked")
    private Mono<ReportResponse> combineAccountsWithMovements(List<AccountRequest> accounts, LocalDate startDate,
                                                              LocalDate endDate) {
        if (accounts.isEmpty()) {
            ReportResponse emptyResponse = new ReportResponse();
            emptyResponse.setAccounts(Collections.emptyList());
            emptyResponse.setMovements(Collections.emptyList());
            return Mono.just(emptyResponse);
        }

        List<Mono<List<MovementResponse>>> movementMonos = accounts.stream().map(account -> {
            return movementQueryService.getMovementsByAccountNumber(account.getAccountNumber(), startDate, endDate)
                    .collectList().onErrorResume(throwable -> {
                        return Mono.just(Collections.emptyList());
                    });
        }).toList();

        return Mono.zip(movementMonos, movements -> {
            ReportResponse response = new ReportResponse();
            response.setAccounts(accounts);

            List<MovementResponse> allMovements = java.util.Arrays.stream(movements)
                    .flatMap(list -> ((List<MovementResponse>) list).stream()).toList();

            response.setMovements(allMovements);
            response.setSummary(calculateSummary(allMovements));
            return response;
        }).onErrorMap(throwable -> {
            return new ExternalServiceException(MOVEMENT_DATA_COMBINATION_FAILED, throwable);
        });
    }

    private ReportResponseSummary calculateSummary(List<MovementResponse> movements) {
        ReportResponseSummary result = new ReportResponseSummary();
        result.setTotalCredits(movements.stream().filter(m -> "CREDIT".equalsIgnoreCase(m.getType()))
                .map(MovementResponse::getValue).reduce(BigDecimal.ZERO, BigDecimal::add));

        result.setTotalDebits(movements.stream().filter(m -> "DEBIT".equalsIgnoreCase(m.getType()))
                .map(MovementResponse::getValue).reduce(BigDecimal.ZERO, BigDecimal::add));

        return result;
    }

}
