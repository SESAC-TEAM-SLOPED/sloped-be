package org.sesac.slopedbe.auth.controller;

import java.io.IOException;
import java.util.Optional;

import org.sesac.slopedbe.auth.exception.MemberAlreadyExistsException;
import org.sesac.slopedbe.auth.exception.MemberNotFoundException;
import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.auth.model.dto.request.LoginRequest;
import org.sesac.slopedbe.auth.model.dto.request.MailVerificationRequest;
import org.sesac.slopedbe.auth.service.LoginServiceImpl;
import org.sesac.slopedbe.auth.service.VerificationService;
import org.sesac.slopedbe.auth.util.JwtUtil;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.repository.MemberRepository;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
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
	private final MemberRepository memberRepository;

	@PostMapping(value="/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest, HttpServletResponse response) throws IOException {
		//Local Login용 method, memberId, password 받아 로그인 진행

		log.info("Login attempt for user: {}", loginRequest.getMemberId());

		// 로그인 - 실패시 error 별로 try-catch 문 진행
		Optional<Member> memberOptional = memberRepository.findByMemberId(loginRequest.getMemberId());
		if (!memberOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found");
		}

		Member member = memberOptional.get();
		String email = memberOptional.get().getId().getEmail();
		String compositeKey = LoginServiceImpl.createCompositeKey(email, MemberOauthType.LOCAL);

		try {
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(compositeKey, loginRequest.getPassword())
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

		final UserDetails userDetails = memberService.loadUserByUsername(compositeKey);
		final String accessToken = jwtUtil.generateAccessToken((GeneralUserDetails)userDetails);
		//Refresh Token DB에 없으면, 생성 후 return, DB에 존재하면 기존 Refresh Token 값 return
		final String refreshToken = verificationService.generateAndSaveRefreshTokenIfNeeded(member, (GeneralUserDetails) userDetails);

		// Access Token을 쿠키에 설정
		Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
		accessTokenCookie.setHttpOnly(false); // 테스트 코드
		accessTokenCookie.setSecure(false); // 테스트 코드
		accessTokenCookie.setPath("/");
		accessTokenCookie.setMaxAge(60 * 5); // 5분
		response.addCookie(accessTokenCookie);

		// Refresh Token을 쿠키에 설정
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
		refreshTokenCookie.setHttpOnly(false); // 테스트 코드
		refreshTokenCookie.setSecure(false); // 테스트 코드
		refreshTokenCookie.setPath("/"); // 쿠키가 유효한 URL 경로 지정
		refreshTokenCookie.setMaxAge(60 * 60 * 24 * 7); // 7일
		response.addCookie(refreshTokenCookie);

		log.info("Access Token added cookie for user {}: {}", loginRequest.getMemberId(), accessToken);
		log.info("Refresh Token added cookie for user {}: {}", loginRequest.getMemberId(), refreshToken);

		// 특정 경로로 리디렉션
		return ResponseEntity.ok().body("Login successful");
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

	@PostMapping(value = "/refresh-access-token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshTokenHeader, HttpServletResponse response) {
		if (refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
		}

		String refreshToken = refreshTokenHeader.substring(7);

		try {
			String email = jwtUtil.extractUserEmail(refreshToken);
			MemberOauthType oauthType = jwtUtil.extractOAuthTypeFromToken(refreshToken);

			Optional<Member> memberOptional = memberRepository.findById(new MemberCompositeKey(email, oauthType));
			Member member = memberOptional.get();
			String compositeKey = LoginServiceImpl.createCompositeKey(email, oauthType);

			boolean isValid = verificationService.validateRefreshToken(member, refreshToken);

			if (isValid) {
				final UserDetails userDetails = memberService.loadUserByUsername(compositeKey);
				final String accessToken = jwtUtil.generateAccessToken((GeneralUserDetails)userDetails);

				// Access Token을 쿠키에 설정
				Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
				accessTokenCookie.setHttpOnly(false); // 테스트 코드
				accessTokenCookie.setSecure(false); // 테스트 코드
				accessTokenCookie.setPath("/");
				accessTokenCookie.setMaxAge(60 * 5); // 5분
				response.addCookie(accessTokenCookie);

				return ResponseEntity.ok("access token");
			} else {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
		}
	}

	@GetMapping("/login-success")
	public void loginSuccess(HttpServletResponse response) throws IOException {
		log.info("JWT Client 전달");
		response.sendRedirect("http://localhost:3000/get-jwt");
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

	@GetMapping("/login/google")
	public void redirectToGoogle(HttpServletResponse response) throws IOException {
		// 구글 로그인 페이지 전달
		response.sendRedirect("http://localhost:8080/oauth2/authorization/google");
	}

}
