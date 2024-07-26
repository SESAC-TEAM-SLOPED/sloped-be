package org.sesac.slopedbe.auth.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.sesac.slopedbe.auth.exception.AuthErrorCode;
import org.sesac.slopedbe.auth.exception.AuthException;
import org.sesac.slopedbe.auth.exception.MemberNotFoundException;
import org.sesac.slopedbe.member.exception.MemberErrorCode;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class MailAuthenticationServiceImpl implements MailAuthenticationService{

	private final RedisTemplate<String, Object> redisTemplate;
	private final JavaMailSender emailSender;
	private final MemberRepository memberRepository;

	private String generateVerificationCode() {
		return String.valueOf(new Random().nextInt(900000) + 100000);
	}

	private void saveVerificationCode(String email, String code) {
		// 인증 코드 메일 발송 전 Redis에 5분간 저장

		redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);
	}

	private void sendVerificationEmail(String to, String code) {
		// 인증 메일 양식

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(to);
		message.setSubject("Your Verification Code");
		message.setText("Your verification code is: " + code);
		message.setFrom("Together@example.com"); // gmail 설정 상 보낸 사람 수정 불가능, 조치예정!
		emailSender.send(message);
	}

	@Override
	public void verifyCode(String email, String code) {
		//redis에 저장된 인증 번호와 입력된 인증 번호 비교

		String storedCode = (String) redisTemplate.opsForValue().get(email);
		if (!code.equals(storedCode)) {
			throw new AuthException(AuthErrorCode.AUTHENTICATION_FAILED);
		}
	}

	@Override
	public void sendRegisterVerificationCode(String email) {
		// 회원 가입용 인증 코드 포함, 인증 메일 전송 method

		MemberOauthType oauthType = MemberOauthType.LOCAL;

		if (memberRepository.existsById(new MemberCompositeKey(email, oauthType))) {
			throw new MemberException(MemberErrorCode.MEMBER_EMAIL_ALREADY_EXISTS);
		}

		String code = generateVerificationCode();
		saveVerificationCode(email, code);
		sendVerificationEmail(email, code);
	}

	@Override
	public void sendFindIdVerificationCode(String email) {
		// 아이디 찾기 용 인증 코드 포함, 인증 메일 전송 method

		MemberOauthType oauthType = MemberOauthType.LOCAL;

		if (memberRepository.findById(new MemberCompositeKey(email, oauthType)).isEmpty()) {
			throw new MemberNotFoundException("해당 이메일이 검색되지 않습니다.");
		}

		String code = generateVerificationCode();
		saveVerificationCode(email, code);
		sendVerificationEmail(email, code);
	}

}
