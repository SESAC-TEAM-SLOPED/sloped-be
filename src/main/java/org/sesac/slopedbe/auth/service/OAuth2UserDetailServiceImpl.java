package org.sesac.slopedbe.auth.service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.member.exception.MemberErrorCode;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class OAuth2UserDetailServiceImpl extends DefaultOAuth2UserService {
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

		request.setAttribute("email", email);
		request.setAttribute("oauthType", oauthType);

		Member member = memberRepository.findById(new MemberCompositeKey(email, oauthType))
			.orElseThrow(() -> {
				log.info("{} 소셜 회원, 회원 가입 필요", oauthType);
				return new MemberException(MemberErrorCode.SOCIAL_MEMBER_NOT_EXISTS);
			});

		return new GeneralUserDetails(member, Collections.singletonList(new SimpleGrantedAuthority(member.getMemberRole().getValue())), paramMap);
	}

	private String getKakaoEmail(Map<String, Object> paramMap) {
		Object value = paramMap.get("kakao_account");

		LinkedHashMap accountMap = (LinkedHashMap) value;
		String email = (String)accountMap.get("email");

		return email;
	}

	private String getNaverEmail(Map<String, Object> paramMap) {
		LinkedHashMap response = (LinkedHashMap) paramMap.get("response");
		String email = (String) response.get("email");

		return email;
	}

	private String getGoogleEmail(Map<String, Object> paramMap) {
		String email = (String)paramMap.get("email");

		return email;
	}

}
