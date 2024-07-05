package org.sesac.slopedbe.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

	private final EmailService emailService;

	@Autowired
	public EmailController(EmailService emailService) {
		this.emailService = emailService;
	}

	@GetMapping("/sendTestEmail")
	public String sendTestEmail(@RequestParam(name = "email") String email) {
		String code = "123456"; // 테스트용 코드
		emailService.sendVerificationEmail(email, code);
		return "Test email sent to " + email;
	}
}
