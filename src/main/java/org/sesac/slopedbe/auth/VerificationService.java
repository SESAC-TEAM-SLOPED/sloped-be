package org.sesac.slopedbe.auth;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class VerificationService {

	private final RedisTemplate<String, Object> redisTemplate;

	public VerificationService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
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
}
