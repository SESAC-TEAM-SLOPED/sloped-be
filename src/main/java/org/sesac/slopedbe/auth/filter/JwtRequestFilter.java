package org.sesac.slopedbe.auth.filter;

import java.io.IOException;

import org.sesac.slopedbe.auth.exception.JwtErrorCode;
import org.sesac.slopedbe.auth.exception.JwtException;
import org.sesac.slopedbe.auth.service.LoginServiceImpl;
import org.sesac.slopedbe.auth.util.JwtUtil;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

	private LoginServiceImpl memberService;
	private JwtUtil jwtUtil;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		boolean shouldNotFilter = path.startsWith("/api/auth/") || path.startsWith("/api/facilities") || path.startsWith("/api/roads") || path.startsWith("/favicon.ico") || path.startsWith("/login/oauth2")
			|| path.startsWith("/api/roadReport/connect-center")
			|| path.startsWith("/api/roadReport/connect-callTaxi")
			|| path.startsWith("/api/roadReport/info/")
			|| path.startsWith("/api/roadReport/get-centerList")
			|| path.startsWith("/api/gpt");
		log.info("Request path: {}, ShouldNotFilter: {}", path, shouldNotFilter);
		return shouldNotFilter;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
		throws ServletException, IOException {

		if (shouldNotFilter(request)) {
			log.info("Skipping JWT filter for path: {}", request.getRequestURI());
			chain.doFilter(request, response);
			return;
		}

		final String authorizationHeader = request.getHeader("Authorization");
		validateAuthorizationHeader(authorizationHeader);

		String token = authorizationHeader.substring(7);
		validateToken(token);

		MemberCompositeKey compositeKey = extractCompositeKey(token);
		authenticateUser(request, compositeKey);

		chain.doFilter(request, response);
	}

	private void validateAuthorizationHeader(String authorizationHeader) {
		if (authorizationHeader == null || authorizationHeader.isBlank()) {
			throw new JwtException(JwtErrorCode.JWT_NOT_FOUND);
		}

		if (!authorizationHeader.startsWith("Bearer ")) {
			throw new JwtException(JwtErrorCode.JWT_PREFIX_INVALID);
		}
	}

	private void validateToken(String token) {
		if (token == null || token.isBlank()) {
			throw new JwtException(JwtErrorCode.JWT_NOT_FOUND);
		}
	}

	private MemberCompositeKey extractCompositeKey(String token) {
		try {
			String email = jwtUtil.extractEmailFromToken(token);
			MemberOauthType oauthType = jwtUtil.extractOAuthTypeFromToken(token);
			return new MemberCompositeKey(email, oauthType);
		} catch (SignatureException e) {
			throw new JwtException(JwtErrorCode.JWT_INVALID);
		} catch (ExpiredJwtException e) {
			throw new JwtException(JwtErrorCode.JWT_EXPIRED);
		}
	}

	private void authenticateUser(HttpServletRequest request, MemberCompositeKey compositeKey) {
		UserDetails userDetails = this.memberService.loadUserByUsername(LoginServiceImpl.createCompositeKey(compositeKey.getEmail(), compositeKey.getOauthType()));
		log.info("Loaded UserDetails: {}", userDetails.getUsername());
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		usernamePasswordAuthenticationToken
			.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
		log.info("Authentication set in SecurityContextHolder: {}", usernamePasswordAuthenticationToken.getPrincipal());
	}
}
