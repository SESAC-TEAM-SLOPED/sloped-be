package org.sesac.slopedbe.auth.controller;

import java.io.IOException;

import org.sesac.slopedbe.auth.exception.MemberAlreadyExistsException;
import org.sesac.slopedbe.auth.exception.MemberNotFoundException;
import org.sesac.slopedbe.auth.model.CustomUserDetails;
import org.sesac.slopedbe.auth.model.dto.request.LoginRequest;
import org.sesac.slopedbe.auth.model.dto.request.MailVerificationRequest;
import org.sesac.slopedbe.auth.model.dto.response.JwtResponse;
import org.sesac.slopedbe.auth.service.LoginServiceImpl;
import org.sesac.slopedbe.auth.service.VerificationService;
import org.sesac.slopedbe.auth.util.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
@Slf4j

public class AuthController {

	private final VerificationService verificationService;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final LoginServiceImpl memberService;
	private final OAuth2AuthorizedClientService authorizedClientService;

	@PostMapping(value="/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {
		//Local Login용 method, memberId, password 받아 로그인 진행


		log.info("Login attempt for user: {}", loginRequest.getMemberId());

		// 로그인 - 실패시 error 별로 try-catch 문 진행
		try {
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getMemberId(), loginRequest.getPassword())
			);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (BadCredentialsException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
		} catch (LockedException e) {
			return ResponseEntity.status(HttpStatus.LOCKED).body("Account locked");
		} catch (DisabledException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Account disabled");
		} catch (AuthenticationException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Authentication failed");
		}

		final UserDetails userDetails = memberService.loadUserByUsername(loginRequest.getMemberId());
		final String jwt = jwtUtil.generateToken((CustomUserDetails)userDetails);

		log.info("JWT generated for user {}: {}", loginRequest.getMemberId(), jwt);
		return ResponseEntity.ok(new JwtResponse(jwt));
	}

	@PostMapping("/send-code/verification-code")
	public ResponseEntity<String> sendRegisterVerificationCode(@RequestBody MailVerificationRequest request) {
		// Client에서 회원가입 중 이메일 인증 요청이 오면 메일로 인증코드 전송 (gmail 활용)
		// 실제 전송 기능은 sendRegisterVerificationCode 에 포함, 여기선 성공, 실패 응답 반환
		// email 전송할 때, 해당 이메일이 존재하면 실패 응답 반환

		String email = request.email();

		log.info("Received request to send verification code to: {}", email);

		try {
			verificationService.sendRegisterVerificationCode(email);
			return ResponseEntity.status(HttpStatus.CREATED).body("Verification code sent to " + email);
		} catch (MemberAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/send-code/recovery-code")
	public ResponseEntity<String> sendFindMemberVerificationCode(@RequestBody MailVerificationRequest request) {
		// Client에서 아이디 찾기, 비밀번호 찾기 중 이메일 인증 요청이 오면 메일로 인증코드 전송 (gmail 활용)
		// email 전송할 때, 해당 이메일이 존재하면 성공 응답 반환

		String email = request.email();
		String id = request.id();

		try {
			verificationService.sendFindIdVerificationCode(email);
			log.info("Received request to send verification code to: {}", email);
			return ResponseEntity.status(HttpStatus.CREATED).body("Verification code sent to " + email);
		} catch (MemberNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/verify-code")
	public ResponseEntity<String> verifyCode(@RequestBody MailVerificationRequest request) {
		// 저장된 인증 코드와 request 코드 검증 method
		verificationService.verifyCode(request.email(), request.code());
		return ResponseEntity.ok("Email verified successfully");
	}

	@GetMapping("/login/kakao")
	public void redirectToKakao(HttpServletResponse response) throws IOException {
		// 카카오 로그인 페이지 전달
		response.sendRedirect("http://localhost:8080/oauth2/authorization/kakao");
	}

	@GetMapping("/login/naver")
	public void redirectToNaver(HttpServletResponse response) throws IOException {
		// 네이버 로그인 페이지 전달
		response.sendRedirect("http://localhost:8080/oauth2/authorization/naver");
	}

	// @GetMapping("/login/oauth2/code/kakao")
	// public void getLoginInfo(OAuth2AuthenticationToken authentication, HttpServletResponse response) throws
	// 	IOException {
	// 	// 인증 성공 후 처리 로직
	// 	// Access Token을 이용해 사용자 정보를 가져오거나 추가 처리를 수행
	// 	log.info("카카오 소셜 로그인 성공이요~");
	//
	// 	// 리다이렉트 처리
	// 	response.sendRedirect("http://localhost:3000/");
	// }

	// @GetMapping("/login/oauth2/code/naver")
	// public String naverCallback(@RequestParam String code, @RequestParam String state) {
	// 	// 받은 code와 state를 사용하여 액세스 토큰을 요청합니다.
	// 	String accessToken = getAccessToken(code, state);
	//
	// 	// 액세스 토큰을 사용하여 사용자 정보를 요청합니다.
	// 	Map<String, Object> userInfo = getUserInfo(accessToken);
	//
	// 	// // 사용자 정보를 사용하여 로그인 처리를 합니다.
	// 	// processUserLogin(userInfo);
	//
	// 	return "redirect:/home";  // 로그인 후 리다이렉트할 페이지
	// }

}
