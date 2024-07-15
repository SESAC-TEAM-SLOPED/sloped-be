package org.sesac.slopedbe.auth.controller;

import org.sesac.slopedbe.auth.exception.MemberAlreadyExistsException;
import org.sesac.slopedbe.auth.exception.MemberNotFoundException;
import org.sesac.slopedbe.auth.model.dto.JwtResponse;
import org.sesac.slopedbe.auth.model.dto.LoginRequest;
import org.sesac.slopedbe.auth.model.dto.MailVerificationRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

	@PostMapping(value="/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) {

		log.info("Login attempt for user: {}", loginRequest.getId());

		try {
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getId(), loginRequest.getPassword())
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

		final UserDetails userDetails = memberService.loadUserByUsername(loginRequest.getId());
		final String jwt = jwtUtil.generateToken(userDetails.getUsername());

		log.info("JWT generated for user {}: {}", loginRequest.getId(), jwt);
		return ResponseEntity.ok(new JwtResponse(jwt));
	}

	@PostMapping("/send-code/verification-code")
	public ResponseEntity<String> sendRegisterVerificationCode(@RequestBody MailVerificationRequest request) {
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
		String email = request.email();
		String id = request.id();

		try {
			if (id != null && !id.isEmpty()) {
				verificationService.sendFindPasswordVerificationCode(id, email);
			} else {
				verificationService.sendFindIdVerificationCode(email);
			}
			log.info("Received request to send verification code to: {}", email);
			return ResponseEntity.status(HttpStatus.CREATED).body("Verification code sent to " + email);
		} catch (MemberNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/verify-code")
	public ResponseEntity<String> verifyCode(@RequestBody MailVerificationRequest request) {
		verificationService.verifyCode(request.email(), request.code());
		return ResponseEntity.ok("Email verified successfully");
	}
}
