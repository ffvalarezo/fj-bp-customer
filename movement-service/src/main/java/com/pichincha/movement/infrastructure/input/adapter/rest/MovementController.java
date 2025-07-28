package com.pichincha.movement.infrastructure.input.adapter.rest;

import java.net.UnknownHostException;
import java.time.LocalDate;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;

import com.pichincha.common.infrastructure.input.adapter.rest.MovementsApi;
import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementRequest;
import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementResponse;
import com.pichincha.common.infrastructure.input.adapter.rest.models.ReportResponse;
import com.pichincha.movement.helper.WebClientHeaderHelper;
import com.pichincha.movement.service.MovementCommandService;
import com.pichincha.movement.service.MovementQueryService;
import com.pichincha.reporting.query.model.GenerateReportQuery;
import com.pichincha.reporting.service.ReportQueryService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class MovementController implements MovementsApi {

	private final MovementCommandService commandService;
	private final MovementQueryService queryService;
	private final ReportQueryService reportQueryService;

	public MovementController(MovementCommandService commandService, MovementQueryService queryService,
			ReportQueryService reportQueryService) {
		this.commandService = commandService;
		this.queryService = queryService;
		this.reportQueryService = reportQueryService;
	}

	@Override
	public Mono<ResponseEntity<MovementResponse>> createMovement(String xGuid, String xChannel, String xMedium,
			String xApp, String xSession, Mono<MovementRequest> movementRequest, ServerWebExchange exchange) {
		return commandService.createMovement(movementRequest)
				.map(created -> ResponseEntity.status(HttpStatus.CREATED).body(created));
	}

	@Override
	public Mono<ResponseEntity<Void>> deleteMovement(Integer id, String xGuid, String xChannel, String xMedium,
			String xApp, String xSession, ServerWebExchange exchange) {
		return commandService.deleteMovement(id).thenReturn(ResponseEntity.noContent().build());
	}

	@Override
	public Mono<ResponseEntity<Flux<MovementResponse>>> getAllMovements(String xGuid, String xChannel, String xMedium,
			String xApp, String xSession, ServerWebExchange exchange) {
		return Mono.just(ResponseEntity.ok(queryService.getAllMovements()));
	}

	@Override
	public Mono<ResponseEntity<MovementResponse>> getMovementById(Integer id, String xGuid, String xChannel,
			String xMedium, String xApp, String xSession, ServerWebExchange exchange) {
		return queryService.getMovementById(id).map(ResponseEntity::ok);
	}

	@Override
	public Mono<ResponseEntity<ReportResponse>> movementsReportGet(LocalDate startDate, LocalDate endDate,
			String customerId, String xGuid, String xChannel, String xMedium, String xApp, String xSession,
			ServerWebExchange exchange) {
		String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
		String jwt = token != null && token.startsWith("Bearer ") ? token.substring(7) : null;
		WebClientHeaderHelper headers = new WebClientHeaderHelper(xGuid, xChannel, xMedium, xApp, xSession, jwt);

		GenerateReportQuery query = GenerateReportQuery.builder().customerId(customerId).startDate(startDate)
				.endDate(endDate).headers(headers).build();
		return Mono.defer(() -> {
			try {
				return reportQueryService.getReport(query).map(ResponseEntity::ok)
						.onErrorResume(UnknownHostException.class, e -> {
							log.error("Error generating report due to unknown host", e);
							return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
						});
			} catch (UnknownHostException e) {
				log.error("Error generating report due to unknown host", e);
				return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());
			}
		});
	}
}