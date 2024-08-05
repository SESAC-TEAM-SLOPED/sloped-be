package org.sesac.slopedbe.auth.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.sesac.slopedbe.auth.model.dto.response.GoogleUserInfoResponse;
import org.sesac.slopedbe.auth.model.dto.response.KakaoUserInfoResponse;
import org.sesac.slopedbe.auth.model.dto.response.NaverUserInfoResponse;
import org.sesac.slopedbe.auth.service.OAuth2UserService;
import org.sesac.slopedbe.auth.service.SocialLoginService;
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
public class SocialLoginController {

	private final SocialLoginService socialLoginService;
	private final OAuth2UserService oAuth2UserService;

	public SocialLoginController(OAuth2UserService oAuth2UserService, SocialLoginService socialLoginService){
		this.socialLoginService = socialLoginService;
		this.oAuth2UserService = oAuth2UserService;
	}

	@Value("${spring.security.oauth2.client.registration.google.client-id}")
	private String googleClientId;
	@Value("${spring.security.oauth2.client.registration.naver.client-id}")
	private String naverClientId;
	@Value("${spring.security.oauth2.client.registration.kakao.client-id}")
	private String kakaoClientId;

	@Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
	private String googleRedirectUri;
	@Value("${spring.security.oauth2.client.registration.naver.redirect-uri}")
	private String naverRedirectUri;
	@Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
	private String kakaoRedirectUri;

	@Operation(summary = "Google Social login", description = "Client에 google REST API KEY, Redirect URI 반환 ")
	@ApiResponse(responseCode = "200", description = "반환 완료")
	@GetMapping("/api/auth/google-login")
	public ResponseEntity<Map<String, String>> getGoogleLoginInfo() {
		Map<String, String> response = new HashMap<>();
		response.put("googleClientId", googleClientId);
		response.put("googleRedirectUri", googleRedirectUri);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Google Social login, Get User Info", description = "Redirect URI로 전달된 code 활용, 유저 정보 추출, login logic 실행")
	@GetMapping("/login/oauth2/code/google")
	public void getGoogleRedirectUri(@RequestParam("code") String code, HttpServletResponse response) throws
		IOException {
		String accessToken = socialLoginService.getAccessTokenFromGoogle(code);
		GoogleUserInfoResponse userInfo = socialLoginService.getGoogleUserInfo(accessToken);

		oAuth2UserService.loginSocialUser(userInfo.getEmail(),"google", response);
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
		String accessToken = socialLoginService.getAccessTokenFromNaver(code, state);
		NaverUserInfoResponse userInfo = socialLoginService.getNaverUserInfo(accessToken);

		oAuth2UserService.loginSocialUser(userInfo.response.getEmail(),"naver", response);
	}

	@Operation(summary = "Kakao Social login", description = "Client에 kakao REST API KEY, Redirect URI 반환 ")
	@ApiResponse(responseCode = "200", description = "반환 완료")
	@GetMapping("/api/auth/kakao-login")
	public ResponseEntity<Map<String, String>> getKakaoLoginInfo() {
		Map<String, String> response = new HashMap<>();
		response.put("kakaoClientId", kakaoClientId);
		response.put("kakaoRedirectUri", kakaoRedirectUri);

		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Kakao Social login, Get User Info", description = "Redirect URI로 전달된 code 활용, 유저 정보 추출, login logic 실행")
	@GetMapping("/login/oauth2/code/kakao")
	public void getKakaoRedirectUri(@RequestParam("code") String code, HttpServletResponse response) throws
		IOException {
		String accessToken = socialLoginService.getAccessTokenFromKakao(code);
		KakaoUserInfoResponse userInfo = socialLoginService.getKakaoUserInfo(accessToken);

		oAuth2UserService.loginSocialUser(userInfo.kakaoAccount.getEmail(),"kakao", response);
	}
}
