package org.sesac.slopedbe.auth.service;

import org.sesac.slopedbe.auth.model.dto.response.GoogleTokenResponse;
import org.sesac.slopedbe.auth.model.dto.response.GoogleUserInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.codec.http.HttpHeaderValues;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class GoogleLoginService {

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String clientId;
	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String clientSecret;
	@Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
	private String googleRedirectUri;

	private final String GAUTH_TOKEN_URL_HOST = "https://oauth2.googleapis.com";
	private final String GAUTH_USER_URL_HOST = "https://www.googleapis.com";

	public String getAccessTokenFromGoogle(String code) {
		GoogleTokenResponse googleTokenResponse = WebClient.create(GAUTH_TOKEN_URL_HOST).post()
			.uri(uriBuilder -> uriBuilder
				.scheme("https")
				.path("/token")
				.queryParam("code", code)
				.queryParam("client_id", clientId)
				.queryParam("client_secret",clientSecret)
				.queryParam("redirect_uri", googleRedirectUri)
				.queryParam("grant_type", "authorization_code")
				.build(true))
			.header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
			.bodyToMono(GoogleTokenResponse.class)
			.block();

		return googleTokenResponse.getAccessToken();
	}

	public GoogleUserInfoResponse getUserInfo(String accessToken) {
		GoogleUserInfoResponse userInfo = WebClient.create(GAUTH_USER_URL_HOST)
			.get()
			.uri(uriBuilder -> uriBuilder
				.scheme("https")
				.path("/userinfo/v2/me")
				.build(true))
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
			.bodyToMono(GoogleUserInfoResponse.class)
			.block();

		return userInfo;
	}
}
