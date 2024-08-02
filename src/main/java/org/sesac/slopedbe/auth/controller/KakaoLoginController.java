package org.sesac.slopedbe.auth.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.sesac.slopedbe.auth.model.dto.response.KakaoUserInfoResponseDto;
import org.sesac.slopedbe.auth.service.KakaoLoginService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class KakaoLoginController {

	private final KakaoLoginService kakaoLoginService;

	@Value("${KAKAO_CLIENT_ID}")
	private String kakaoClientId;

	@Value("${KAKAO_REDIRECT_URI}")
	private String redirectUri;

	public KakaoLoginController(KakaoLoginService kakaoLoginService) {
		this.kakaoLoginService = kakaoLoginService;
	}

	@GetMapping("/api/auth/kakao-login")
	public ResponseEntity<Map<String, String>> getKakaoLoginInfo() {
		Map<String, String> response = new HashMap<>();
		response.put("kakaoClientId", kakaoClientId);
		response.put("redirectUri", redirectUri);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/login/oauth2/code/kakao")
	public ResponseEntity<Map<String, String>> getKakaoRedirectUri(@RequestParam("code") String code) throws IOException {
		String accessToken = kakaoLoginService.getAccessTokenFromKakao(code);
		KakaoUserInfoResponseDto userInfo = kakaoLoginService.getUserInfo(accessToken);




		log.info("userInfo = {}", userInfo.kakaoAccount.email);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
