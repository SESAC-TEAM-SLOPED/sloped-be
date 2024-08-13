package org.sesac.slopedbe.auth.service;

import static org.sesac.slopedbe.auth.service.LoginServiceImpl.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.sesac.slopedbe.auth.exception.AuthErrorCode;
import org.sesac.slopedbe.auth.exception.AuthException;
import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.auth.model.dto.request.LoginRequest;
import org.sesac.slopedbe.auth.util.JwtUtil;
import org.sesac.slopedbe.member.exception.MemberErrorCode;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TokenAuthenticationService {

	private final MemberRepository memberRepository;
	private final JwtUtil jwtUtil;
	private final LoginServiceImpl loginService;
	private final AuthenticationManager authenticationManager;

	@Value("${JWT_ACCESS_EXPIRATION_TIME}")
	private int accessTokenExpirationTime;

	@Value("${JWT_REFRESH_EXPIRATION_TIME}")
	private int refreshTokenExpirationTime;

	public TokenAuthenticationService (MemberRepository memberRepository, JwtUtil jwtUtil, LoginServiceImpl loginService, AuthenticationManager authenticationManager){
		this.memberRepository = memberRepository;
		this.jwtUtil = jwtUtil;
		this.loginService = loginService;
		this.authenticationManager = authenticationManager;
	}

	@PostConstruct
	private void init() {
		accessTokenExpirationTime = accessTokenExpirationTime / 1000;
		refreshTokenExpirationTime = refreshTokenExpirationTime / 1000;
	}

	private void saveRefreshToken(Member member, String refreshToken) throws MemberException {
		member.setRefreshToken(refreshToken);
		memberRepository.save(member);
	}

	private boolean validateRefreshToken(Member member, String refreshToken) throws MemberException {
		return member.getRefreshToken().equals(refreshToken);
	}

	private String generateAndSaveRefreshTokenIfNeeded(GeneralUserDetails userDetails) throws MemberException {
		Member member = userDetails.getMember();
		String refreshToken = member.getRefreshToken();

		if (refreshToken == null || !jwtUtil.validateToken(refreshToken, userDetails.getUsername())) {
			refreshToken = jwtUtil.generateRefreshToken(userDetails);
			saveRefreshToken(member, refreshToken);
			return refreshToken;
		} else {
			return refreshToken;
		}
	}

	public ResponseEntity<Map<String, String>> localLoginToken (LoginRequest loginRequest, HttpServletResponse response) throws IOException {
		Optional<Member> memberOptional = memberRepository.findByMemberId(loginRequest.memberId());

		if (!memberOptional.isPresent()) {
			throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
		}

		Member member = memberOptional.get();
		String email = member.getId().getEmail();
		String compositeKey = createCompositeKey(email, MemberOauthType.LOCAL);

		try {
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(compositeKey, loginRequest.password())
			);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (BadCredentialsException e) {
			throw new AuthException(AuthErrorCode.BAD_CREDENTIALS);
		} catch (LockedException e) {
			throw new AuthException(AuthErrorCode.ACCOUNT_LOCKED);
		} catch (DisabledException e) {
			throw new AuthException(AuthErrorCode.ACCOUNT_DISABLED);
		} catch (AuthenticationException e) {
			throw new AuthException(AuthErrorCode.AUTHENTICATION_EXCEPTION);
		}

		final UserDetails userDetails = loginService.loadUserByUsername(compositeKey);
		final String accessToken = jwtUtil.generateAccessToken((GeneralUserDetails) userDetails);
		final String refreshToken = generateAndSaveRefreshTokenIfNeeded((GeneralUserDetails) userDetails);

		setCookie(response, "refreshToken", refreshToken, refreshTokenExpirationTime);

		Map<String, String> successResponse = new HashMap<>();
		successResponse.put("message", "Login successful");
		successResponse.put("refreshToken", refreshToken);
		return ResponseEntity.ok()
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
			.body(successResponse);
	}

	public ResponseEntity<Map<String, String>> renewToken(String expiredAccessToken, String refreshToken, HttpServletResponse response) throws IOException {
		try {
			String email = jwtUtil.extractEmailFromToken(expiredAccessToken);
			MemberOauthType oauthType = jwtUtil.extractOAuthTypeFromToken(expiredAccessToken);
			Optional<Member> memberOptional = memberRepository.findById(new MemberCompositeKey(email, oauthType));

			if (!memberOptional.isPresent()) {
				throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
			}

			Member member = memberOptional.get();
			String compositeKey = createCompositeKey(email, oauthType);
			boolean isValid = validateRefreshToken(member, refreshToken);

			if (isValid) {
				final UserDetails userDetails = loginService.loadUserByUsername(compositeKey);
				final String accessToken = jwtUtil.generateAccessToken((GeneralUserDetails) userDetails);

				Map<String, String> successResponse = new HashMap<>();
				successResponse.put("message", "Access token 갱신 완료");
				return ResponseEntity.ok()
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.body(successResponse);
			} else {
				final UserDetails userDetails = loginService.loadUserByUsername(compositeKey);
				final String accessToken = jwtUtil.generateAccessToken((GeneralUserDetails) userDetails);
				final String newRefreshToken = jwtUtil.generateRefreshToken((GeneralUserDetails) userDetails);
				saveRefreshToken(member, newRefreshToken);
				setCookie(response, "refreshToken", newRefreshToken, refreshTokenExpirationTime);

				Map<String, String> successResponse = new HashMap<>();
				successResponse.put("message", "Access token, refresh token 갱신 완료");
				successResponse.put("refreshToken", newRefreshToken);
				return ResponseEntity.ok()
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
					.body(successResponse);
			}
		} catch (Exception e) {
			throw new AuthException(AuthErrorCode.AUTHENTICATION_FAILED);
		}
	}

	public void createSocialAuthenticationCookies(HttpServletResponse response, GeneralUserDetails userDetails) throws IOException {
		Member member = userDetails.getMember();
		String accessToken = jwtUtil.generateAccessToken(userDetails);
		String refreshToken = generateAndSaveRefreshTokenIfNeeded(userDetails);

		setCookie(response, "refreshToken", refreshToken, refreshTokenExpirationTime);

		String encodedAccessToken = URLEncoder.encode(accessToken, StandardCharsets.UTF_8);
		String redirectUrl = String.format("https://www.togetheroad.me/get-jwt?accessToken=%s", encodedAccessToken);

		response.sendRedirect(redirectUrl);
	}

	private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setHttpOnly(true);
		cookie.setSecure(true);
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		cookie.setDomain("togetheroad.me");
		response.addCookie(cookie);
	}



}
