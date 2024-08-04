package org.sesac.slopedbe.auth.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.sesac.slopedbe.auth.model.dto.response.KakaoUserInfoResponse;
import org.sesac.slopedbe.auth.service.KakaoLoginService;
import org.sesac.slopedbe.auth.service.OAuth2UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class KakaoLoginController {

	private final KakaoLoginService kakaoLoginService;
	private final OAuth2UserService oAuth2UserService;

	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String kakaoClientId;

	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String kakaoRedirectUri;

	public KakaoLoginController(KakaoLoginService kakaoLoginService, OAuth2UserService oAuth2UserService){
		this.kakaoLoginService = kakaoLoginService;
		this.oAuth2UserService = oAuth2UserService;
	}

	@Operation(summary = "Kakao Social login", description = "Client에 kakao REST API KEY, Redirect URI 반환 ")
	@ApiResponse(responseCode = "200", description = "반환 완료")
	@GetMapping("/api/auth/kakao-login")
	public ResponseEntity<Map<String, String>> getKakaoLoginInfo() {
		Map<String, String> response = new HashMap<>();
		response.put("kakaoClientId", kakaoClientId);
		response.put("redirectUri", kakaoRedirectUri);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Kakao Social login, Get User Info", description = "Redirect URI로 전달된 code 활용, 유저 정보 추출, login logic 실행")
	@GetMapping("/login/oauth2/code/kakao")
	public void getKakaoRedirectUri(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
		String accessToken = kakaoLoginService.getAccessTokenFromKakao(code);
		KakaoUserInfoResponse userInfo = kakaoLoginService.getUserInfo(accessToken);

		oAuth2UserService.loginSocialUser(userInfo.kakaoAccount.getEmail(),"kakao", response);
	}
}
