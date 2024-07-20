package org.sesac.slopedbe.auth.service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class CustomOAuth2UserServiceImpl extends DefaultOAuth2UserService implements CustomOAuth2UserService {

	private final MemberRepository memberRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		Map<String, Object> paramMap = oAuth2User.getAttributes();

		String email = getkakaoEmail(paramMap);
		log.info("Extracted email:"+ email);

		Optional<Member> memberOpt = memberRepository.findByEmail(email);
		if (memberOpt.isEmpty()) {
			//소셜 로그인 가입
			log.info("소셜 아이디 가입 필요");
		}

		return oAuth2User;
	}

	@Override
	public String getkakaoEmail(Map<String, Object> paramMap) {

		Object value = paramMap.get("kakao_account");

		LinkedHashMap accountMap = (LinkedHashMap) value;
		String email = (String)accountMap.get("email");

		log.info("email :" + email);

		return email;
	}
}
