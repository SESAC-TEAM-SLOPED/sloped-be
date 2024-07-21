package org.sesac.slopedbe.auth.service;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface CustomOAuth2UserService extends OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	// OAuth2User loadUser(OAuth2UserRequest userRequest);
	String getkakaoEmail(Map<String, Object> paramMap);
	String getNaverEmail(Map<String, Object> paramMap);
}
