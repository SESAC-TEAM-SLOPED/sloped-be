package org.sesac.slopedbe.auth.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.sesac.slopedbe.auth.exception.SocialMemberNotExistsException;
import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class OAuth2UserDetailServiceImpl extends DefaultOAuth2UserService {
	// 소셜 로그인에서 사용되는 클래스, 따로 import 되지 않는다.

	private final MemberRepository memberRepository;
	private final HttpServletRequest request;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		Map<String, Object> paramMap = oAuth2User.getAttributes();

		String email = null;
		MemberOauthType oauthType;

		String registrationId = userRequest.getClientRegistration().getRegistrationId();

		if ("kakao".equals(registrationId)) {
			oauthType = MemberOauthType.KAKAO;
			email = getKakaoEmail(paramMap);
		} else if ("naver".equals(registrationId)) {
			oauthType = MemberOauthType.NAVER;
			email = getNaverEmail(paramMap);
		} else if ("google".equals(registrationId)) {
			oauthType = MemberOauthType.GOOGLE;
			email = getGoogleEmail(paramMap);
		} else {
			throw new OAuth2AuthenticationException("Unsupported OAuth2 provider");
		}

		// 정보를 HttpServletRequest에 설정 > 소셜 회원 가입 용도
		request.setAttribute("email", email);
		request.setAttribute("oauthType", oauthType);

		Member member = memberRepository.findById(new MemberCompositeKey(email, oauthType))
			.orElseThrow(() -> {
				log.info("{} 소셜 회원, 회원 가입 필요", oauthType);
				OAuth2Error oauth2Error = new OAuth2Error("social_member_not_exists", "회원 가입 필요", null);
				return new OAuth2AuthenticationException(oauth2Error, new SocialMemberNotExistsException("회원 가입 필요"));
			});

		return new GeneralUserDetails(member, Collections.singletonList(new SimpleGrantedAuthority(member.getMemberRole().getValue())), paramMap);
	}

	//아래 email 구하는 함수는 외부에서 사용하지 않아 private으로 구현
	private String getKakaoEmail(Map<String, Object> paramMap) {
		// Response에서 email 구하는 method
		Object value = paramMap.get("kakao_account");

		LinkedHashMap accountMap = (LinkedHashMap) value;
		String email = (String)accountMap.get("email");

		return email;
	}

	private String getNaverEmail(Map<String, Object> paramMap) {
		// Response에서 email 구하는 method
		// KAKAO랑 Response 응답 형태가 달라서 method 차이 발생

		LinkedHashMap response = (LinkedHashMap) paramMap.get("response");
		String email = (String) response.get("email");

		return email;
	}

	private String getGoogleEmail(Map<String, Object> paramMap) {
		// Response에서 email 구하는 method
		// KAKAO랑 Response 응답 형태가 달라서 method 차이 발생
		String email = (String)paramMap.get("email");
		return email;
	}

}
