package org.sesac.slopedbe.auth.service;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.sesac.slopedbe.auth.exception.AuthErrorCode;
import org.sesac.slopedbe.auth.exception.AuthException;
import org.sesac.slopedbe.auth.exception.MemberNotFoundException;
import org.sesac.slopedbe.auth.exception.SocialMemberNotExistsException;
import org.sesac.slopedbe.member.exception.MemberErrorCode;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VerificationServiceImpl implements VerificationService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final JavaMailSender emailSender;
	private final MemberRepository memberRepository;

	private String generateVerificationCode() {
		// 6자리 랜덤 인증코드 생성
		return String.valueOf(new Random().nextInt(900000) + 100000);
	}

	private void saveVerificationCode(String email, String code) {
		// 인증 코드 메일 발송 전 Redis에 5분간 저장
		// email 과 code 함께 저장, 중복 방지
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

		// 이메일 이미 존재하면 오류
		if (memberRepository.existsById(new MemberCompositeKey(email, oauthType))) {
			throw new MemberException(MemberErrorCode.MEMBER_EMAIL_ALREADY_EXISTS);
		}
		// 인증 코드 생성
		String code = generateVerificationCode();
		// 인증 코드 Redis에 비교용으로 저장
		saveVerificationCode(email, code);
		// 인증 코드 전송
		sendVerificationEmail(email, code);
	}

	@Override
	public void sendFindIdVerificationCode(String email) {
		// 아이디 찾기 용 인증 코드 포함, 인증 메일 전송 method

		MemberOauthType oauthType = MemberOauthType.LOCAL;

		// 이메일 존재하지 않으면 오류
		if (memberRepository.findById(new MemberCompositeKey(email, oauthType)).isEmpty()) {
			throw new MemberNotFoundException("해당 이메일이 검색되지 않습니다.");
		}

		// 인증 코드 생성
		String code = generateVerificationCode();
		// 인증 코드 Redis에 비교용으로 저장
		saveVerificationCode(email, code);
		// 인증 코드 전송
		sendVerificationEmail(email, code);
	}

	@Override
	public void sendSocialRegisterInformation(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws
		IOException {

		// 소셜 회원 가입을 위해 email, OAuthType 데이터 보내는 메서드

		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		if (exception.getCause() instanceof SocialMemberNotExistsException) {
			String email = (String)request.getAttribute("email");
			MemberOauthType oauthType = (MemberOauthType)request.getAttribute("oauthType");

			//소셜 회원가입에 전달할 Response
			if (email != null && oauthType != null) {

				String redirectUrl = String.format("http://localhost:3000/login/register/social?email=%s&oauthType=%s", email, oauthType.name());

				response.sendRedirect(redirectUrl);
			} else {
				response.sendRedirect("http://localhost:3000/login?error=true");
			}
		}

	}


	// @Override
	// public void sendFindPasswordVerificationCode(String id, String email) {
	// 	//비밀번호 찾기 용도
	// 	//이 메서드는 Id 찾기 sendFindIdVerificationCode method와 병합 예정
	//
	// 	if (memberRepository.findByEmail(email).isEmpty() || memberRepository.findByMemberId(id).isEmpty()) {
	// 		throw new MemberNotFoundException("해당 아이디 또는 이메일이 조회되지 않습니다.");
	// 	}
	// 	String code = generateVerificationCode();
	// 	saveVerificationCode(email, code);
	// 	sendVerificationEmail(email, code);
	// }
}
