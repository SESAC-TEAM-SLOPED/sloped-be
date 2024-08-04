package org.sesac.slopedbe.auth.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.sesac.slopedbe.auth.model.dto.response.NaverUserInfoResponse;
import org.sesac.slopedbe.auth.service.NaverLoginService;
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
public class NaverLoginController {

	private final NaverLoginService naverLoginService;
	private final OAuth2UserService oAuth2UserService;

	@Value("${spring.security.oauth2.client.registration.naver.client-id}")
	private String naverClientId;

	@Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
	private String naverRedirectUri;

	public NaverLoginController(NaverLoginService naverLoginService,OAuth2UserService oAuth2UserService){
		this.naverLoginService = naverLoginService;
		this.oAuth2UserService = oAuth2UserService;
	}

	@Operation(summary = "Naver Social login", description = "Client에 naver REST API KEY, Redirect URI 반환 ")
	@ApiResponse(responseCode = "200", description = "반환 완료")
	@GetMapping("/api/auth/naver-login")
	public ResponseEntity<Map<String, String>> getNaverLoginInfo() {
		Map<String, String> response = new HashMap<>();
		response.put("naverClientId", naverClientId);
		response.put("naverRedirectUri", naverRedirectUri);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Naver Social login, Get User Info", description = "Redirect URI로 전달된 code 활용, 유저 정보 추출, login logic 실행")
	@GetMapping("/login/oauth2/code/naver")
	public void getKakaoRedirectUri(@RequestParam("code") String code, @RequestParam("state") String state, HttpServletResponse response) throws
		IOException {
		String accessToken = naverLoginService.getAccessTokenFromNaver(code, state);
		NaverUserInfoResponse userInfo = naverLoginService.getNaverUserInfo(accessToken);

		oAuth2UserService.loginSocialUser(userInfo.response.getEmail(),"naver", response);
	}

}
