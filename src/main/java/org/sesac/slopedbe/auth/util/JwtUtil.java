package org.sesac.slopedbe.auth.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.sesac.slopedbe.auth.exception.JwtErrorCode;
import org.sesac.slopedbe.auth.exception.JwtException;
import org.sesac.slopedbe.auth.model.GeneralUserDetails;
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

	public JwtUtil(@Value("${JWT_SECRET_KEY}") String secretKey,
		@Value("${JWT_ACCESS_EXPIRATION_TIME}") long accessTokenExpirationTime,
		@Value("${JWT_REFRESH_EXPIRATION_TIME}") long refreshTokenExpirationTime) {
		this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
		this.accessTokenExpirationTime = accessTokenExpirationTime;
		this.refreshTokenExpirationTime = refreshTokenExpirationTime;
	}

	public String generateAccessToken(GeneralUserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("nickname", userDetails.getMember().getNickname());
		claims.put("oauthType", userDetails.getUserOauthType());

		return Jwts.builder()
			.setClaims(claims)
			.setSubject(userDetails.getUsername())
			.setIssuedAt(new Date(System.currentTimeMillis()))
			.setExpiration(new Date(System.currentTimeMillis() + accessTokenExpirationTime))
			.signWith(key, SignatureAlgorithm.HS256)
			.compact();
	}

	public String generateRefreshToken(GeneralUserDetails userDetails) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("oauthType", userDetails.getUserOauthType());
		return Jwts.builder()
			.setClaims(claims)
			.setSubject(userDetails.getUsername())
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

	public Boolean validateToken(String token, String userEmail) {
		final String extractedUserEmail = extractAllClaims(token).getSubject();
		return (extractedUserEmail.equals(userEmail) && !isTokenExpired(token));
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}
}
