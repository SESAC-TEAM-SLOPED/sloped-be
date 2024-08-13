package org.sesac.slopedbe.auth.util;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.sesac.slopedbe.auth.exception.JwtErrorCode;
import org.sesac.slopedbe.auth.exception.JwtException;
import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.auth.service.FakeIdService;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtUtil {
	private final SecretKey key;
	private final long accessTokenExpirationTime;
	private final long refreshTokenExpirationTime;
	private final FakeIdService fakeIdService;

	public JwtUtil(@Value("${JWT_SECRET_KEY}") String secretKey,
		@Value("${JWT_ACCESS_EXPIRATION_TIME}") long accessTokenExpirationTime,
		@Value("${JWT_REFRESH_EXPIRATION_TIME}") long refreshTokenExpirationTime,
		FakeIdService fakeIdService) {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
		this.accessTokenExpirationTime = accessTokenExpirationTime;
		this.refreshTokenExpirationTime = refreshTokenExpirationTime;
		this.fakeIdService = fakeIdService;
	}

	public String generateAccessToken(GeneralUserDetails userDetails) {
		String accessToken = generateToken(userDetails, accessTokenExpirationTime);
		return accessToken;
	}

	public String generateRefreshToken(GeneralUserDetails userDetails) {
		String refreshToken =generateToken(userDetails, refreshTokenExpirationTime);
		return refreshToken;
	}

	private String generateToken(GeneralUserDetails userDetails, long expirationTime) {
		Map<String, Object> claims = new HashMap<>();
		String fakeId;

		try {
			fakeId = fakeIdService.generateFakeId(userDetails.getUsername(), userDetails.getUserOauthType());
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			log.info("Error generating fake ID", e);
			fakeId = "default_value";
		}

		if (expirationTime == accessTokenExpirationTime) {
			claims.put("nickname", userDetails.getMember().getNickname());
			claims.put("authType", userDetails.getUserOauthType().toString());
		}

		return Jwts.builder()
			.setClaims(claims)
			.setSubject(fakeId)
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	private Claims extractAllClaims(String token) {
		Jws<Claims> jwsClaims = Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token);
		return jwsClaims.getBody();
	}

	public String extractEmailFromToken(String token){
		try {
			Jws<Claims> claimsJws = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);

			Claims body = claimsJws.getBody();
			String fakeId = body.getSubject();
			return fakeIdService.extractEmailFromFakeId(fakeId);
		} catch (ExpiredJwtException e) {
			Claims body = e.getClaims();
			String fakeId = body.getSubject();
			return fakeIdService.extractEmailFromFakeId(fakeId);
		} catch (Exception e) {
			log.error("Error extracting email from token", e);
			return null;
		}
	}

	public MemberOauthType extractOAuthTypeFromToken(String token) {
		try {
			Jws<Claims> claimsJws = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);

			Claims body = claimsJws.getBody();
			String fakeId = body.getSubject();
			return fakeIdService.extractOAuthTypeFromFakeId(fakeId);
		} catch (ExpiredJwtException e) {
			Claims body = e.getClaims();
			String fakeId = body.getSubject();
			return fakeIdService.extractOAuthTypeFromFakeId(fakeId);
		} catch (Exception e) {
			log.error("Error extracting email from token", e);
			return null;
		}
	}

	public MemberCompositeKey extractCompositeKey(String token) {
		try {
			String email = extractEmailFromToken(token);
			MemberOauthType oauthType = extractOAuthTypeFromToken(token);

			log.info("Extracted email: {}, oauthType: {}", email, oauthType);
			return new MemberCompositeKey(email, oauthType);
		} catch (SignatureException e) {
			throw new JwtException(JwtErrorCode.JWT_INVALID);
		} catch (ExpiredJwtException e) {
			throw new JwtException(JwtErrorCode.JWT_EXPIRED);
		}
	}

	public MemberCompositeKey getMemberCKFromHeader() {
		HttpServletRequest request = ((ServletRequestAttributes)Objects.requireNonNull(
			RequestContextHolder.getRequestAttributes())).getRequest();
		final String authorizationHeader = request.getHeader("Authorization");

		if (authorizationHeader == null || authorizationHeader.isBlank()) {
			return null;
		}

		String token = authorizationHeader.substring(7);
		if(token.isBlank()) {
			return null;
		}
		return extractCompositeKey(token);
	}

	private Date extractExpirationDate(String token) {
		return extractAllClaims(token).getExpiration();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpirationDate(token).before(new Date());
	}

	//수정
	public Boolean validateToken(String token, String userEmail) {
		final String extractedUserEmail = extractAllClaims(token).getSubject();
		return (extractedUserEmail.equals(userEmail) && !isTokenExpired(token));
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
}
