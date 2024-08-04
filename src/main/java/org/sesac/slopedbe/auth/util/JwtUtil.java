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
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
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

	private Claims extractAllClaims(String token) {
		Jws<Claims> jwsClaims = Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token);
		return jwsClaims.getBody();
	}

	public String extractEmailFromToken(String token) {
		log.info("Extracting email from token: {}", token);

		try {
			Jws<Claims> claimsJws = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token);

			Claims body = claimsJws.getBody();
			String email = body.getSubject();
			return email;
		} catch (ExpiredJwtException e) {
			Claims body = e.getClaims();
			String email = body.getSubject();
			return email;
		} catch (Exception e) {
			log.error("Error extracting email from token", e);
			return null;
		}
	}

	public MemberOauthType extractOAuthTypeFromToken(String token) {

		try {
			String oauthTypeValue = getClaimFromToken(token, claims -> claims.get("oauthType", String.class));
			return MemberOauthType.valueOf(oauthTypeValue);
		} catch (ExpiredJwtException e) {
			Claims body = e.getClaims();
			String oauthTypeValue = body.get("oauthType", String.class);
			return MemberOauthType.valueOf(oauthTypeValue);
		} catch (Exception e) {
			log.error("Error extracting OAuth type from token", e);
			return null;
		}
	}

	public String extractNicknameFromToken(String token) {
		log.info("Extracting nickname from token: {}", token);

		try {
			String nickname = getClaimFromToken(token, claims -> claims.get("nickname", String.class));
			return nickname;
		} catch (ExpiredJwtException e) {
			Claims body = e.getClaims();
			String nickname = body.get("nickname", String.class);
			return nickname;
		} catch (Exception e) {
			log.error("Error extracting nickname from token", e);
			return null;
		}
	}

	private Date extractExpirationDate(String token) {
		return extractAllClaims(token).getExpiration();
	}

	private Boolean isTokenExpired(String token) {
		return extractExpirationDate(token).before(new Date());
	}

	public Boolean validateToken(String token, String userEmail) {
		final String extractedUserEmail = extractAllClaims(token).getSubject();
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
