package org.sesac.slopedbe.auth;

import org.sesac.slopedbe.auth.DTO.VerificationRequest;
import org.sesac.slopedbe.config.SecurityConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class VerificationController {

	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

	private final VerificationService verificationService;

	@PostMapping("/sendCode/register")
	public ResponseEntity<String> sendRegisterVerificationCode(@RequestBody VerificationRequest request) {
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
	public ResponseEntity<String> sendFindMemberVerificationCode(@RequestBody VerificationRequest request) {
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
	public ResponseEntity<String> verifyCode(@RequestBody VerificationRequest request) {
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
