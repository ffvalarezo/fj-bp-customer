package com.pichincha.reporting.service.impl;

import static com.pichincha.movement.infrastructure.constants.ApplicationConstants.ACCOUNT_SERVICE_CONNECTION_ERROR;
import static com.pichincha.movement.infrastructure.constants.ApplicationConstants.ACCOUNT_SERVICE_TIMEOUT;
import static com.pichincha.movement.infrastructure.constants.ApplicationConstants.ACCOUNT_SERVICE_UNAVAILABLE;
import static com.pichincha.movement.infrastructure.constants.ApplicationConstants.CONNECTION_TIMEOUT_MS;
import static com.pichincha.movement.infrastructure.constants.ApplicationConstants.CUSTOMER_ID_REQUIRED;
import static com.pichincha.movement.infrastructure.constants.ApplicationConstants.MOVEMENT_DATA_COMBINATION_FAILED;
import static com.pichincha.movement.infrastructure.constants.ApplicationConstants.READ_TIMEOUT_MS;
import static com.pichincha.movement.infrastructure.constants.ApplicationConstants.REPORT_GENERATION_FAILED;
import static com.pichincha.movement.infrastructure.constants.ApplicationConstants.RESPONSE_TIMEOUT_SECONDS;
import static com.pichincha.movement.infrastructure.constants.ApplicationConstants.TCP_KEEP_IDLE_SECONDS;
import static com.pichincha.movement.infrastructure.constants.ApplicationConstants.TCP_KEEP_INTERVAL_SECONDS;
import static com.pichincha.movement.infrastructure.constants.ApplicationConstants.WRITE_TIMEOUT_MS;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;
import com.pichincha.common.infrastructure.input.adapter.rest.models.MovementResponse;
import com.pichincha.common.infrastructure.input.adapter.rest.models.ReportResponse;
import com.pichincha.common.infrastructure.input.adapter.rest.models.ReportResponseSummary;
import com.pichincha.movement.helper.WebClientHeaderHelper;
import com.pichincha.movement.infrastructure.exception.ExternalServiceException;
import com.pichincha.movement.service.MovementQueryService;
import com.pichincha.reporting.service.ReportService;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

	private final WebClient.Builder webClientBuilder;
	private final String endPointAccount;
	private final MovementQueryService movementQueryService;
	private final HttpClient httpClient;

	public ReportServiceImpl(WebClient.Builder webClientBuilder,
			@Value("${external.account-service.url}") String endPointAccount,
			@Value("${external.customer-service.url}") String endPointCustomer,
			MovementQueryService movementQueryService) {
		this.webClientBuilder = webClientBuilder;
		this.endPointAccount = endPointAccount;
		this.movementQueryService = movementQueryService;
		this.httpClient = createHttpClient();
	}

	private HttpClient createHttpClient() {
		return HttpClient.create().option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECTION_TIMEOUT_MS)
				.option(ChannelOption.SO_KEEPALIVE, true).option(EpollChannelOption.TCP_KEEPIDLE, TCP_KEEP_IDLE_SECONDS)
				.option(EpollChannelOption.TCP_KEEPINTVL, TCP_KEEP_INTERVAL_SECONDS)
				.responseTimeout(Duration.ofSeconds(RESPONSE_TIMEOUT_SECONDS)).doOnConnected(connection -> {
					connection.addHandlerLast(new ReadTimeoutHandler(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS));
					connection.addHandlerLast(new WriteTimeoutHandler(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS));
				});
	}

	private Mono<List<AccountRequest>> getAccountsByCustomerId(String customerId, WebClientHeaderHelper headers) {
		log.debug("Fetching accounts for customer: {}", customerId);

		WebClient webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
				.baseUrl(endPointAccount + "/accounts/filter")
				.defaultHeaders(httpHeaders -> headers.applyTo(httpHeaders))
				.defaultUriVariables(Collections.singletonMap("url", endPointAccount + "/accounts/filter")).build();

		return webClient.method(HttpMethod.GET).uri("/" + customerId).retrieve().bodyToFlux(AccountRequest.class)
				.collectList().timeout(Duration.ofSeconds(RESPONSE_TIMEOUT_SECONDS))
				.onErrorMap(WebClientResponseException.class, ex -> {
					log.error("Account service error for customer {}: {} - {}", customerId, ex.getStatusCode(),
							ex.getResponseBodyAsString());
					return new ExternalServiceException(ACCOUNT_SERVICE_UNAVAILABLE + customerId, ex);
				}).onErrorMap(java.util.concurrent.TimeoutException.class, ex -> {
					log.error("Timeout calling account service for customer {}", customerId);
					return new ExternalServiceException(ACCOUNT_SERVICE_TIMEOUT + customerId, ex);
				}).onErrorMap(java.net.ConnectException.class, ex -> {
					log.error("Connection error to account service for customer {}", customerId);
					return new ExternalServiceException(ACCOUNT_SERVICE_CONNECTION_ERROR + customerId, ex);
				});
	}

	@Override
	public Mono<ReportResponse> generateReport(String customerId, LocalDate start, LocalDate end, String xGuid,
			String xChannel, String xMedium, String xApp, String xSession, String token) {

		log.info("Generating report for customer: {}, period: {} to {}", customerId, start, end);

		if (customerId == null || customerId.trim().isEmpty()) {
			return Mono.error(new IllegalArgumentException(CUSTOMER_ID_REQUIRED));
		}

		WebClientHeaderHelper headers = new WebClientHeaderHelper(xGuid, xChannel, xMedium, xApp, xSession, token);

		return getAccountsByCustomerId(customerId, headers)
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
		log.debug("Combining {} accounts with movements", accounts.size());

		if (accounts.isEmpty()) {
			log.warn("No accounts found, returning empty report");
			ReportResponse emptyResponse = new ReportResponse();
			emptyResponse.setAccounts(Collections.emptyList());
			emptyResponse.setMovements(Collections.emptyList());
			return Mono.just(emptyResponse);
		}

		List<Mono<List<MovementResponse>>> movementMonos = accounts.stream().map(account -> {
			log.debug("Fetching movements for account: {}", account.getAccountNumber());
			return movementQueryService.getMovementsByAccountNumber(account.getAccountNumber(), startDate, endDate)
					.collectList().onErrorResume(throwable -> {
						log.warn("Failed to fetch movements for account {}: {}", account.getAccountNumber(),
								throwable.getMessage());
						return Mono.just(Collections.emptyList());
					});
		}).toList();

		return Mono.zip(movementMonos, movements -> {
			log.debug("Combining all movements data");
			ReportResponse response = new ReportResponse();
			response.setAccounts(accounts);

			List<MovementResponse> allMovements = java.util.Arrays.stream(movements)
					.flatMap(list -> ((List<MovementResponse>) list).stream()).toList();

			response.setMovements(allMovements);
			response.setSummary(calculateSummary(allMovements));
			log.debug("Combined report contains {} accounts and {} movements", accounts.size(), allMovements.size());
			return response;
		}).onErrorMap(throwable -> {
			log.error("Error combining accounts with movements: {}", throwable.getMessage());
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
