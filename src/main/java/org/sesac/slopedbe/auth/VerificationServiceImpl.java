package org.sesac.slopedbe.auth;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VerificationServiceImpl implements VerificationService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final JavaMailSender emailSender;
	private final MemberRepository memberRepository;

	private String generateVerificationCode() {
		return String.valueOf(new Random().nextInt(900000) + 100000);
		//인증번호는 숫자 6자리로 구현
	}

	private void saveVerificationCode(String email, String code) {
		redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES); // 5분 유효
	}

	private void sendVerificationEmail(String to, String code) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("Your Verification Code");
		message.setText("Your verification code is: " + code);
		message.setFrom("Together@example.com"); // gmail 설정 상 수정 불가능, 조치예정!
		emailSender.send(message);
	}

	@Override
	public boolean verifyCode(String email, String code) {
		String storedCode = (String) redisTemplate.opsForValue().get(email);
		return code.equals(storedCode);
	}

	@Override
	public void sendRegisterVerificationCode(String email) {
		if (memberRepository.findByEmail(email).isPresent()) {
			throw new MemberAlreadyExistsException("Member with email " + email + " already exists.");
		}

		String code = generateVerificationCode();
		saveVerificationCode(email, code);
		sendVerificationEmail(email, code);
	}

	@Override
	public void sendFindIdVerificationCode(String email) {
		if (memberRepository.findByEmail(email).isEmpty()) {
			throw new MemberNotFoundException("해당 이메일이 조회되지 않습니다.");
		}

		String code = generateVerificationCode();
		saveVerificationCode(email, code);
		sendVerificationEmail(email, code);
	}

	@Override
	public void sendFindPasswordVerificationCode(String id, String email) {
		//비밀번호 찾기 용도
		if (memberRepository.findByEmail(email).isEmpty() || memberRepository.findById(id).isEmpty()) {
			throw new MemberNotFoundException("해당 아이디 또는 이메일이 조회되지 않습니다.");
		}
		String code = generateVerificationCode();
		saveVerificationCode(email, code);
		sendVerificationEmail(email, code);
	}
}
