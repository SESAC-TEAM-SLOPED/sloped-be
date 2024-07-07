package org.sesac.slopedbe.auth;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class VerificationController {

	private static final Logger log = LoggerFactory.getLogger(VerificationController.class);
	private final VerificationService verificationService;

	@PostMapping("/sendCode")
	public ResponseEntity<String> sendVerificationCode(@RequestHeader Map<String, String> headers, @RequestBody Map<String, String> request) {
		// 헤더 로깅
		headers.forEach((key, value) -> {
			log.info("Header '{}' = {}", key, value);
		});

		String email = request.get("email");
		log.info("Received request to send verification code to: {}", email);
		verificationService.sendVerificationCode(email);
		return ResponseEntity.ok("Verification code sent to " + email);
	}

	@PostMapping("/verifyCode")
	public ResponseEntity<String> verifyCode(@RequestParam("email") String email, @RequestParam("code") String code) {
		boolean isVerified = verificationService.verifyCode(email, code);
		if (isVerified) {
			return ResponseEntity.ok("Email verified successfully");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code");
		}
	}

	// @GetMapping("/sendTestEmail")
	// public String sendTestEmail(@RequestParam(name = "email") String email) {
	// 	// 실제 메일 발송 테스트
	// 	log.info("Received request to send verification code to: {}", email);
	// 	verificationService.sendVerificationCode(email);
	// 	return "Test email sent to " + email;
	// }

}
