package org.sesac.slopedbe.auth.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
	private final SecretKey key;
	private final long accessTokenExpirationTime;
	private final long refreshTokenExpirationTime;

	public JwtUtil(@Value("${JWT_SECRET_KEY}") String secretKey,
		@Value("${JWT_ACCESS_EXPIRATION_TIME}") long accessTokenExpirationTime,
		@Value("${JWT_REFRESH_EXPIRATION_TIME}") long refreshTokenExpirationTime) {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
		this.accessTokenExpirationTime = accessTokenExpirationTime;
		this.refreshTokenExpirationTime = refreshTokenExpirationTime;
	}

	// Access Token 생성
	public String generateAccessToken(GeneralUserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("nickname", userDetails.getMember().getNickname());
		claims.put("oauthType", userDetails.getUserOauthType());

		return Jwts.builder()
			.setClaims(claims)
			.setSubject(userDetails.getUsername()) // email 주소 포함
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	// Refresh Token 생성
	public String generateRefreshToken(GeneralUserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		// 탈취되어도 문제 없게 Refresh Token에는 다른 정보들 제외 예정 !!
		claims.put("nickname", userDetails.getMember().getNickname());
		claims.put("oauthType", userDetails.getUserOauthType());

		return Jwts.builder()
			.setClaims(claims)
			.setSubject(userDetails.getUsername()) // email 주소 포함
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpirationTime))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public String extractUserEmail(String token) {
		//이메일 추출
		return extractAllClaims(token).getSubject();
	}

	public Date extractExpirationDate(String token) {
		return extractAllClaims(token).getExpiration();
	}

	private Claims extractAllClaims(String token) {
		Jws<Claims> jwsClaims = Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token);
		return jwsClaims.getBody();
	}

	public String extractEmailFromToken(String token) {
		return extractUserEmail(token);
	}

	public String extractNicknameFromToken(String token) {
		return getClaimFromToken(token, claims -> claims.get("nickname", String.class));
	}

	public MemberOauthType extractOAuthTypeFromToken(String token) {
		String oauthTypeValue = getClaimFromToken(token, claims -> claims.get("oauthType", String.class));
		return MemberOauthType.valueOf(oauthTypeValue);
	}

	private Boolean isTokenExpired(String token) {
		return extractExpirationDate(token).before(new Date());
	}

	public Boolean validateToken(String token, String userEmail) {
		// 토큰이 유효하면 true(1) 반환
		final String extractedUserEmail = extractUserEmail(token);
		return (extractedUserEmail.equals(userEmail) && !isTokenExpired(token));
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public long getAccessTokenExpirationTime() {
		return accessTokenExpirationTime;
	}

	public long getRefreshTokenExpirationTime() {
		return refreshTokenExpirationTime;
	}
}
