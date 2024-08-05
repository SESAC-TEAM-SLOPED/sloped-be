package org.sesac.slopedbe.auth.service;

import java.io.IOException;
import java.util.Optional;

import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class OAuth2UserService {

	private final MemberRepository memberRepository;
	private LoginServiceImpl memberService;
	private final TokenAuthenticationService tokenAuthenticationService;

	public void loginSocialUser(String email, String socialLoginType, HttpServletResponse response) throws IOException {
		MemberOauthType oauthType;
		if ("kakao".equals(socialLoginType)) {
			oauthType = MemberOauthType.KAKAO;
		} else if ("naver".equals(socialLoginType)) {
			oauthType = MemberOauthType.NAVER;
		} else if ("google".equals(socialLoginType)) {
			oauthType = MemberOauthType.GOOGLE;
		} else {
			throw new OAuth2AuthenticationException("Unsupported OAuth2 provider");
		}

		Optional<Member> memberOptional = memberRepository.findById(new MemberCompositeKey(email, oauthType));

		if (memberOptional.isEmpty()) {
			log.info("{} 소셜 회원, 회원 가입 필요", oauthType);
			String redirectUrl = String.format("http://localhost:3000/login/register/social?email=%s&oauthType=%s", email, oauthType.name());
			response.sendRedirect(redirectUrl);
		}

		MemberCompositeKey compositeKey = new MemberCompositeKey(email, oauthType);
		GeneralUserDetails userDetails = this.memberService.loadUserByUsername(LoginServiceImpl.createCompositeKey(compositeKey.getEmail(), compositeKey.getOauthType()));
		tokenAuthenticationService.createSocialAuthenticationCookies(response, userDetails);
	}
}
