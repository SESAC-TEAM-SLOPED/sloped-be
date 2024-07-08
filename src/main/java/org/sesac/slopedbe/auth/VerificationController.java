package org.sesac.slopedbe.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class VerificationController {

	private static final Logger log = LoggerFactory.getLogger(VerificationController.class);
	private final VerificationService verificationService;

	@PostMapping("/sendCode")
	public ResponseEntity<String> sendVerificationCode(@RequestBody VerificationRequest request) {

		String email = request.getEmail();
		log.info("Received request to send verification code to: {}", email);
		verificationService.sendVerificationCode(email);
		return ResponseEntity.ok("Verification code sent to " + email);
	}

	@PostMapping("/verifyCode")
	public ResponseEntity<String> verifyCode(@RequestBody VerificationRequest request) {
		boolean isVerified = verificationService.verifyCode(request.getEmail(), request.getCode());
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
