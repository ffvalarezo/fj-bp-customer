package com.pichincha.movement.helper;

import lombok.Getter;
import org.springframework.http.HttpHeaders;

@Getter
public class WebClientHeaderHelper {

	private final String xGuid;
	private final String xChannel;
	private final String xMedium;
	private final String xApp;
	private final String xSession;
	private final String authorization;

	public WebClientHeaderHelper(String xGuid, String xChannel, String xMedium, String xApp, String xSession,
			String authorization) {
		this.xGuid = clean(xGuid);
		this.xChannel = clean(xChannel);
		this.xMedium = clean(xMedium);
		this.xApp = clean(xApp);
		this.xSession = clean(xSession);
		this.authorization = clean(authorization);
	}

	private String clean(String value) {
		return value != null ? value.trim() : "";
	}

	public void applyTo(HttpHeaders headers) {
		headers.set("x-guid", xGuid);
		headers.set("x-channel", xChannel);
		headers.set("x-medium", xMedium);
		headers.set("x-app", xApp);
		headers.set("x-session", xSession);
		headers.set("Authorization", "Bearer " + authorization);
	}
}