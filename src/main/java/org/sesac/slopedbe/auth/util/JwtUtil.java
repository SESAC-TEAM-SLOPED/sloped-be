package org.sesac.slopedbe.auth.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.sesac.slopedbe.auth.model.GeneralUserDetails;
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
	private final long expirationTime;

	public JwtUtil(@Value("${JWT_SECRET_KEY}") String secretKey,
		@Value("${JWT_EXPIRATION_TIME}") long expirationTime) {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
		this.expirationTime = expirationTime;
	}

	//Local User
	public String generateToken(GeneralUserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("nickname", userDetails.getMember().getNickname());
		claims.put("oauthType", userDetails.getUserOauthType());

		return Jwts.builder()
			.setClaims(claims)
			.setSubject(userDetails.getUsername()) //email 주소 포함
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public String extractUserId(String token) {
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
		return getClaimFromToken(token, claims -> claims.get("email", String.class));
	}

	public String extractNicknameFromToken(String token) {
		return getClaimFromToken(token, claims -> claims.get("nickname", String.class));
	}

	private Boolean isTokenExpired(String token) {
		return extractExpirationDate(token).before(new Date());
	}

	public Boolean validateToken(String token, String username) {
		final String extractedUsername = extractUserId(token);
		return (extractedUsername.equals(username) && !isTokenExpired(token));
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
}
