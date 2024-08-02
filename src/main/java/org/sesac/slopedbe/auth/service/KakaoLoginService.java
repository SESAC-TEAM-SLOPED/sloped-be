package org.sesac.slopedbe.auth.service;

import org.sesac.slopedbe.auth.model.dto.response.KakaoTokenResponseDto;
import org.sesac.slopedbe.auth.model.dto.response.KakaoUserInfoResponseDto;
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
public class KakaoLoginService {

	@Value("${KAKAO_CLIENT_ID}")
	private String clientId;
	@Value("${KAKAO_CLIENT_SECRET}")
	private String clientSecret;
	private final String KAUTH_TOKEN_URL_HOST = "https://kauth.kakao.com";
	private final String KAUTH_USER_URL_HOST = "https://kapi.kakao.com";

	public String getAccessTokenFromKakao(String code) {
		KakaoTokenResponseDto kakaoTokenResponseDto = WebClient.create(KAUTH_TOKEN_URL_HOST).post()
			.uri(uriBuilder -> uriBuilder
				.scheme("https")
				.path("/oauth/token")
				.queryParam("grant_type", "authorization_code")
				.queryParam("client_id", clientId)
				.queryParam("code", code)
				.queryParam("client_secret",clientSecret)
				.build(true))
			.header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
			.retrieve()
			//TODO : Custom Exception
			.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
			.bodyToMono(KakaoTokenResponseDto.class)
			.block();

		return kakaoTokenResponseDto.getAccessToken();
	}

	public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
		KakaoUserInfoResponseDto userInfo = WebClient.create(KAUTH_USER_URL_HOST)
			.get()
			.uri(uriBuilder -> uriBuilder
				.scheme("https")
				.path("/v2/user/me")
				.build(true))
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken) // access token 인가
			.header(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())
			.retrieve()
			//TODO : Custom Exception
			.onStatus(HttpStatusCode::is4xxClientError, clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
			.onStatus(HttpStatusCode::is5xxServerError, clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
			.bodyToMono(KakaoUserInfoResponseDto.class)
			.block();

		return userInfo;
	}

}
