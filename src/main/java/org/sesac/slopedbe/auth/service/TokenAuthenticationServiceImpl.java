package org.sesac.slopedbe.auth.service;

import static org.sesac.slopedbe.auth.service.LoginServiceImpl.*;

import java.io.IOException;
import java.util.Optional;

import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.auth.model.dto.request.LoginRequest;
import org.sesac.slopedbe.auth.util.JwtUtil;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.http.HttpStatus;
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

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class TokenAuthenticationServiceImpl implements TokenAuthenticationService{

	private final MemberRepository memberRepository;
	private final JwtUtil jwtUtil;
	private final LoginServiceImpl loginService;
	private final AuthenticationManager authenticationManager;

	private void saveRefreshToken(Member member, String refreshToken) throws MemberException {
		member.setRefreshToken(refreshToken);
		memberRepository.save(member);
	}


	private boolean validateRefreshToken(Member member, String refreshToken) throws MemberException {
		return member.getRefreshToken().equals(refreshToken);
	}

	// Refresh Token 검증 및 생성
	private String generateAndSaveRefreshTokenIfNeeded(Member member, GeneralUserDetails userDetails) throws MemberException {
		// 항상 refreshToken return

		String refreshToken = member.getRefreshToken();

		if (refreshToken == null || !jwtUtil.validateToken(refreshToken, userDetails.getUsername())) {
			refreshToken = jwtUtil.generateRefreshToken(userDetails);
			saveRefreshToken(member, refreshToken);
			return refreshToken;
		} else {
			return refreshToken;
		}

	}

	@Override
	public ResponseEntity<?> createAuthenticationToken(LoginRequest loginRequest, HttpServletResponse response) throws
		IOException {

		Optional<Member> memberOptional = memberRepository.findByMemberId(loginRequest.getMemberId());
		if (!memberOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found");
		}

		Member member = memberOptional.get();
		String email = memberOptional.get().getId().getEmail();
		String compositeKey = createCompositeKey(email, MemberOauthType.LOCAL);

		try {
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(compositeKey, loginRequest.getPassword())
			);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (BadCredentialsException | LockedException | DisabledException e) {
			throw e;
		} catch (AuthenticationException e) {
			throw e;
		}

		final UserDetails userDetails = loginService.loadUserByUsername(compositeKey);
		final String accessToken = jwtUtil.generateAccessToken((GeneralUserDetails) userDetails);
		final String refreshToken = generateAndSaveRefreshTokenIfNeeded(member, (GeneralUserDetails) userDetails);

		setCookie(response, "accessToken", accessToken, 60 * 5);  // 5분
		setCookie(response, "refreshToken", refreshToken, 60 * 60 * 24 * 7);  // 7일

		log.info("Access Token added cookie for user {}: {}", loginRequest.getMemberId(), accessToken);
		log.info("Refresh Token added cookie for user {}: {}", loginRequest.getMemberId(), refreshToken);

		return ResponseEntity.ok().body("Login successful");
	}

	@Override
	public ResponseEntity<?> refreshAccessToken(String refreshTokenHeader, String expiredAccessToken, HttpServletResponse response) throws IOException {
		if (refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
		}

		String refreshToken = refreshTokenHeader.substring(7);

		try {
			log.info("try?");
			String email = jwtUtil.extractEmailFromToken(expiredAccessToken);
			MemberOauthType oauthType = jwtUtil.extractOAuthTypeFromToken(expiredAccessToken);

			Optional<Member> memberOptional = memberRepository.findById(new MemberCompositeKey(email, oauthType));

			if (!memberOptional.isPresent()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Member not found");
			}

			Member member = memberOptional.get();
			String compositeKey = createCompositeKey(email, oauthType);

			boolean isValid = validateRefreshToken(member, refreshToken);

			if (isValid) {
				final UserDetails userDetails = loginService.loadUserByUsername(compositeKey);
				final String accessToken = jwtUtil.generateAccessToken((GeneralUserDetails) userDetails);

				setCookie(response, "accessToken", accessToken, 60 * 5);  // 5분

				return ResponseEntity.ok("Access token refreshed");
			} else {
				final UserDetails userDetails = loginService.loadUserByUsername(compositeKey);
				final String accessToken = jwtUtil.generateAccessToken((GeneralUserDetails) userDetails);
				final String newRefreshToken = jwtUtil.generateRefreshToken((GeneralUserDetails) userDetails);
				saveRefreshToken(member, newRefreshToken);

				setCookie(response, "accessToken", accessToken, 60 * 5);  // 5분
				setCookie(response, "refreshToken", newRefreshToken, 60 * 60 * 24 * 7);  // 7일

				return ResponseEntity.ok("Access token and refresh token refreshed");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
		}
	}

	@Override
	public void createSocialAuthenticationCookies(HttpServletResponse response, GeneralUserDetails userDetails) throws IOException {
		String accessToken = jwtUtil.generateAccessToken(userDetails);
		String refreshToken = jwtUtil.generateRefreshToken(userDetails);

		Member member = userDetails.getMember();

		saveRefreshToken(member, refreshToken);

		setCookie(response, "accessToken", accessToken, 60 * 5);  // 5분
		setCookie(response, "refreshToken", refreshToken, 60 * 60 * 24 * 7);  // 7일

		log.info("Generated access token: {}", accessToken);
		log.info("Generated refresh token: {}", refreshToken);
	}


	private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
		Cookie cookie = new Cookie(name, value);
		cookie.setHttpOnly(false);  // 테스트 코드
		cookie.setSecure(false);    // 테스트 코드
		cookie.setPath("/");
		cookie.setMaxAge(maxAge);
		response.addCookie(cookie);
	}

}
