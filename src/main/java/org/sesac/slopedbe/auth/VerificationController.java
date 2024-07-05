package org.sesac.slopedbe.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class VerificationController {

	private final VerificationService verificationService;

	@PostMapping("/sendCode")
	public ResponseEntity<String> sendVerificationCode(@RequestParam("email") String email) {
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
	// 	String code = "123456";
	// 	verificationService.sendVerificationEmail(email, code);
	// 	return "Test email sent to " + email;
	// }

}
