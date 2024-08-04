package org.sesac.slopedbe.auth.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.sesac.slopedbe.auth.model.dto.response.GoogleUserInfoResponse;
import org.sesac.slopedbe.auth.service.GoogleLoginService;
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
public class GoogleLoginController {

	private final GoogleLoginService googleLoginService;
	private final OAuth2UserService oAuth2UserService;

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String googleClientId;

	@Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
	private String googleRedirectUri;

	public GoogleLoginController(GoogleLoginService googleLoginService, OAuth2UserService oAuth2UserService){
		this.googleLoginService = googleLoginService;
		this.oAuth2UserService = oAuth2UserService;
	}

	@Operation(summary = "Google Social login", description = "Client에 google REST API KEY, Redirect URI 반환 ")
	@ApiResponse(responseCode = "200", description = "반환 완료")
	@GetMapping("/api/auth/google-login")
	public ResponseEntity<Map<String, String>> getKakaoLoginInfo() {
		Map<String, String> response = new HashMap<>();
		response.put("googleClientId", googleClientId);
		response.put("googleRedirectUri", googleRedirectUri);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Google Social login, Get User Info", description = "Redirect URI로 전달된 code 활용, 유저 정보 추출, login logic 실행")
	@GetMapping("/login/oauth2/code/google")
	public void getKakaoRedirectUri(@RequestParam("code") String code, HttpServletResponse response) throws
		IOException {
		String accessToken = googleLoginService.getAccessTokenFromGoogle(code);
		GoogleUserInfoResponse userInfo = googleLoginService.getUserInfo(accessToken);

		oAuth2UserService.loginSocialUser(userInfo.getEmail(),"google", response);
	}


}
