package org.sesac.slopedbe.auth.controller;

import org.sesac.slopedbe.auth.exception.MemberAlreadyExistsException;
import org.sesac.slopedbe.auth.exception.MemberNotFoundException;
import org.sesac.slopedbe.auth.model.DTO.JwtResponse;
import org.sesac.slopedbe.auth.model.DTO.LoginRequest;
import org.sesac.slopedbe.auth.model.DTO.MailVerificationRequest;
import org.sesac.slopedbe.auth.service.VerificationService;
import org.sesac.slopedbe.auth.util.JwtUtil;
import org.sesac.slopedbe.common.config.SecurityConfig;
import org.sesac.slopedbe.member.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

	private final VerificationService verificationService;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final MemberService memberService;

	@PostMapping(value="/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {

		try {
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getId(), loginRequest.getPassword())
			);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}

		final UserDetails userDetails = memberService.loadUserByUsername(loginRequest.getId());
		final String jwt = jwtUtil.generateToken(userDetails.getUsername());

		log.info("JWT generated for user {}: {}", loginRequest.getId(), jwt);
		return ResponseEntity.ok(new JwtResponse(jwt));
	}

	@PostMapping("/sendCode/register")
	public ResponseEntity<String> sendRegisterVerificationCode(@RequestBody MailVerificationRequest request) {
		String email = request.getEmail();

		log.info("Received request to send verification code to: {}", email);

		try {
			verificationService.sendRegisterVerificationCode(email);
			return ResponseEntity.ok("Verification code sent to " + email);
		} catch (MemberAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
		}
	}

	@PostMapping("/sendCode/findMember")
	public ResponseEntity<String> sendFindMemberVerificationCode(@RequestBody MailVerificationRequest request) {
		String email = request.getEmail();
		String id = request.getId();

		try {
			if (id != null && !id.isEmpty()) {
				verificationService.sendFindPasswordVerificationCode(id, email);
			} else {
				verificationService.sendFindIdVerificationCode(email);
			}
			log.info("Received request to send verification code to: {}", email);
			return ResponseEntity.ok("Verification code sent to " + email);
		} catch (MemberNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

	@PostMapping("/verifyCode")
	public ResponseEntity<String> verifyCode(@RequestBody MailVerificationRequest request) {
		String email = request.getEmail();
		String code = request.getCode();

		boolean isVerified = verificationService.verifyCode(email, code);

		if (isVerified) {
			return ResponseEntity.ok("Email verified successfully");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code");
		}
	}

	@ExceptionHandler(MemberAlreadyExistsException.class)
	public ResponseEntity<String> handleMemberAlreadyExistsException(MemberAlreadyExistsException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
	}

	@ExceptionHandler(MemberNotFoundException.class)
	public ResponseEntity<String> handleMemberNotFoundException(MemberNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
	}

}
