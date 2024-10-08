package org.sesac.slopedbe.auth.service;

import static org.sesac.slopedbe.auth.service.LoginServiceImpl.*;

import java.io.IOException;
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



	private boolean validateRefreshToken(Member member, String refreshToken) throws MemberException {
		return member.getRefreshToken().equals(refreshToken);
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
		final String refreshToken = jwtUtil.generateRefreshToken((GeneralUserDetails) userDetails);

		setCookie(response, "refreshToken", refreshToken, refreshTokenExpirationTime);

		Map<String, String> successResponse = new HashMap<>();
		successResponse.put("message", "Login successful");
		successResponse.put("accessToken", accessToken);
		return ResponseEntity.ok(successResponse);
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
		String refreshToken = jwtUtil.generateRefreshToken(userDetails);
		setCookie(response, "refreshToken", refreshToken, refreshTokenExpirationTime);
		String redirectUrl = String.format("https://www.togetheroad.me/get-jwt");

		response.sendRedirect(redirectUrl);
	}

	public ResponseEntity<Map<String, String>> createAccessTokenUsingRefreshToken(HttpServletResponse response, String refreshToken){
		log.info("createAccessTokenUsingRefreshToken 시작");

		String email = jwtUtil.extractEmailFromToken(refreshToken);
		MemberOauthType oauthType = jwtUtil.extractOAuthTypeFromToken(refreshToken);
		String compositeKey = createCompositeKey(email, oauthType);
		final UserDetails userDetails = loginService.loadUserByUsername(compositeKey);
		final String accessToken = jwtUtil.generateAccessToken((GeneralUserDetails) userDetails);
		Map<String, String> successResponse = new HashMap<>();
		successResponse.put("message", "Access token 발급 완료");
		successResponse.put("accessToken", accessToken);
		log.info("accessToken: {}",accessToken);

		return ResponseEntity.ok(successResponse);
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
