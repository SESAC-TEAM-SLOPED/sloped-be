package org.sesac.slopedbe.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class VerificationController {

	private final VerificationService verificationService;
	private final EmailService emailService;

	public VerificationController(VerificationService verificationService, EmailService emailService) {
		this.verificationService = verificationService;
		this.emailService = emailService;
	}

	@PostMapping("/sendCode")
	public ResponseEntity<String> sendVerificationCode(@RequestParam String email) {
		String code = verificationService.generateVerificationCode();
		verificationService.saveVerificationCode(email, code);
		emailService.sendVerificationEmail(email, code);
		return ResponseEntity.ok("Verification code sent to " + email);
	}

	@PostMapping("/verifyCode")
	public ResponseEntity<String> verifyCode(@RequestParam String email, @RequestParam String code) {
		boolean isVerified = verificationService.verifyCode(email, code);
		if (isVerified) {
			return ResponseEntity.ok("Email verified successfully");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification code");
		}
	}

}
