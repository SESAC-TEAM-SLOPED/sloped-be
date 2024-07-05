package org.sesac.slopedbe.auth;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service

public class VerificationService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final JavaMailSender emailSender;

	public VerificationService(RedisTemplate<String, Object> redisTemplate, JavaMailSender emailSender) {
		this.redisTemplate = redisTemplate;
		this.emailSender = emailSender;
	}

	public String generateVerificationCode() {
		return String.valueOf(new Random().nextInt(900000) + 100000); // 6자리 인증번호 생성
	}

	public void saveVerificationCode(String email, String code) {
		redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES); // 5분 동안 유효
	}

	public boolean verifyCode(String email, String code) {
		String storedCode = (String) redisTemplate.opsForValue().get(email);
		return code.equals(storedCode);
	}

	public void sendVerificationEmail(String to, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("Your Verification Code");
		message.setText("Your verification code is: " + code);
		message.setFrom("Together@example.com"); // gmail 설정 상 수정 불가능, 조치예정!
		emailSender.send(message);
	}
}
