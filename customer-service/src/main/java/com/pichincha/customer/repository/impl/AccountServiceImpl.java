package com.pichincha.customer.repository.impl;

import com.pichincha.common.infrastructure.input.adapter.rest.models.AccountRequest;
import com.pichincha.customer.helper.WebClientHeaderHelper;
import com.pichincha.customer.infrastructure.exception.ExternalServiceException;
import com.pichincha.customer.repository.AccountService;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static com.pichincha.customer.infrastructure.constants.ApplicationConstants.*;

@Repository
@Slf4j
public class AccountServiceImpl implements AccountService {

	private final WebClient.Builder webClientBuilder;
	private final String endPointAccount;
	private final HttpClient httpClient;

	public AccountServiceImpl(WebClient.Builder webClientBuilder,
			@Value("${external.account-service.url}") String endPointAccount) {
		this.webClientBuilder = webClientBuilder;
		this.endPointAccount = endPointAccount;
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

	@Override
	public Mono<AccountRequest> createAccountReactive(AccountRequest accountRequest, WebClientHeaderHelper headers) {
		WebClient webClient = webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient))
				.baseUrl(endPointAccount + "/accounts").defaultHeaders(headers::applyTo)
				.defaultUriVariables(Collections.singletonMap("url", endPointAccount + "/accounts")).build();

		return webClient.post().uri("").bodyValue(accountRequest).retrieve().bodyToMono(AccountRequest.class)
				.timeout(Duration.ofSeconds(RESPONSE_TIMEOUT_SECONDS))
				.onErrorMap(WebClientResponseException.class,
						ex -> new ExternalServiceException(ACCOUNT_SERVICE_UNAVAILABLE, ex))
				.onErrorMap(java.util.concurrent.TimeoutException.class,
						ex -> new ExternalServiceException(ACCOUNT_SERVICE_TIMEOUT, ex))
				.onErrorMap(java.net.ConnectException.class,
						ex -> new ExternalServiceException(ACCOUNT_SERVICE_CONNECTION_ERROR, ex));
	}
}
