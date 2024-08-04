package org.sesac.slopedbe.auth.service;

import org.sesac.slopedbe.auth.model.dto.response.NaverTokenResponse;
import org.sesac.slopedbe.auth.model.dto.response.NaverUserInfoResponse;
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
public class NaverLoginService {

	@Value("${spring.security.oauth2.client.registration.naver.client-id}")
	private String clientId;
	@Value("${spring.security.oauth2.client.registration.naver.client-secret}")
	private String clientSecret;

	private final String NAUTH_TOKEN_URL_HOST = "https://nid.naver.com";
	private final String NAUTH_USER_URL_HOST = "https://openapi.naver.com";

	public String getAccessTokenFromNaver(String code, String state){
		NaverTokenResponse naverTokenResponse = WebClient.create(NAUTH_TOKEN_URL_HOST).post()
			.uri(uriBuilder -> uriBuilder
				.scheme("https")
				.path("/oauth2.0/token")
				.queryParam("grant_type", "authorization_code")
				.queryParam("client_id", clientId)
				.queryParam("client_secret",clientSecret)
				.queryParam("code", code)
				.queryParam("state", state)
				.build(true)
			)
			.header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
			.bodyToMono(NaverTokenResponse.class)
			.block();

		return naverTokenResponse.getAccessToken();
	}

	public NaverUserInfoResponse getNaverUserInfo(String accessToken) {
		NaverUserInfoResponse userInfo = WebClient.create(NAUTH_USER_URL_HOST)
			.get()
			.uri(uriBuilder -> uriBuilder
				.scheme("https")
				.path("/v1/nid/me")
				.build(true))
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
			.bodyToMono(NaverUserInfoResponse.class)
			.block();

		return userInfo;
	}
}
