package org.sesac.slopedbe.auth.service;

import org.sesac.slopedbe.auth.model.dto.response.GoogleTokenResponse;
import org.sesac.slopedbe.auth.model.dto.response.GoogleUserInfoResponse;
import org.sesac.slopedbe.auth.model.dto.response.KakaoTokenResponse;
import org.sesac.slopedbe.auth.model.dto.response.KakaoUserInfoResponse;
import org.sesac.slopedbe.auth.model.dto.response.NaverTokenResponse;
import org.sesac.slopedbe.auth.model.dto.response.NaverUserInfoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Service
public class SocialLoginService {

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String googleClientId;
	@Value("${spring.security.oauth2.client.registration.google.client-secret}")
	private String googleClientSecret;
	@Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
	private String googleRedirectUri;

	@Value("${spring.security.oauth2.client.registration.naver.client-id}")
	private String naverClientId;
	@Value("${spring.security.oauth2.client.registration.naver.client-secret}")
	private String naverClientSecret;

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String kakaoClientId;
	@Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
	private String kakaoClientSecret;

	private final String GAUTH_TOKEN_URL_HOST = "https://oauth2.googleapis.com";
	private final String GAUTH_USER_URL_HOST = "https://www.googleapis.com";
	private final String NAUTH_TOKEN_URL_HOST = "https://nid.naver.com";
	private final String NAUTH_USER_URL_HOST = "https://openapi.naver.com";
	private final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
	private final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";

	public String getAccessTokenFromGoogle(String code) {
		WebClient webClient = WebClient.builder()
			.baseUrl(GAUTH_TOKEN_URL_HOST)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.build();

		GoogleTokenResponse googleTokenResponse = webClient.post()
			.uri(uriBuilder -> uriBuilder
				.path("/token")
				.queryParam("code", code)
				.queryParam("client_id", googleClientId)
				.queryParam("client_secret", googleClientSecret)
				.queryParam("redirect_uri", googleRedirectUri)
				.queryParam("grant_type", "authorization_code")
				.build())
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
			.bodyToMono(GoogleTokenResponse.class)
			.block();

		if (googleTokenResponse == null) {
			throw new RuntimeException("Failed to retrieve access token from Google");
		}

		return googleTokenResponse.getAccessToken();
	}

	public GoogleUserInfoResponse getGoogleUserInfo(String accessToken) {
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

	public String getAccessTokenFromNaver(String code, String state) {
		WebClient webClient = WebClient.builder()
			.baseUrl(NAUTH_TOKEN_URL_HOST)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.build();

		NaverTokenResponse naverTokenResponse = webClient.post()
			.uri(uriBuilder -> uriBuilder
				.path("/oauth2.0/token")
				.queryParam("grant_type", "authorization_code")
				.queryParam("client_id", naverClientId)
				.queryParam("client_secret", naverClientSecret)
				.queryParam("code", code)
				.queryParam("state", state)
				.build())
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
			.bodyToMono(NaverTokenResponse.class)
			.block();

		if (naverTokenResponse == null) {
			throw new RuntimeException("Failed to retrieve access token from Naver");
		}

		return naverTokenResponse.getAccessToken();
	}

	public NaverUserInfoResponse getNaverUserInfo(String accessToken) {
		WebClient webClient = WebClient.builder()
			.baseUrl(NAUTH_USER_URL_HOST)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.build();

		NaverUserInfoResponse userInfo = webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/v1/nid/me")
				.build())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
			.bodyToMono(NaverUserInfoResponse.class)
			.block();

		return userInfo;
	}

	public String getAccessTokenFromKakao(String code) {
		WebClient webClient = WebClient.builder()
			.baseUrl(KAUTH_TOKEN_URL_HOST)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.build();

		KakaoTokenResponse kakaoTokenResponse = webClient.post()
			.uri(uriBuilder -> uriBuilder
				.path("/oauth/token")
				.queryParam("grant_type", "authorization_code")
				.queryParam("client_id", kakaoClientId)
				.queryParam("client_secret", kakaoClientSecret)
				.queryParam("code", code)
				.build())
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
			.bodyToMono(KakaoTokenResponse.class)
			.block();

		if (kakaoTokenResponse == null) {
			throw new RuntimeException("Failed to retrieve access token from Kakao");
		}

		return kakaoTokenResponse.getAccessToken();
	}

	public KakaoUserInfoResponse getKakaoUserInfo(String accessToken) {
		WebClient webClient = WebClient.builder()
			.baseUrl(KAUTH_USER_URL_HOST)
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
			.build();

		KakaoUserInfoResponse userInfo = webClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/v2/user/me")
				.build())
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
			.bodyToMono(KakaoUserInfoResponse.class)
			.block();

		if (userInfo == null) {
			throw new RuntimeException("Failed to retrieve user info from Kakao");
		}

		return userInfo;
	}
}
