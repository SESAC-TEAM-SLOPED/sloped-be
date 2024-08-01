package org.sesac.slopedbe.auth.filter;

import java.io.IOException;

import org.sesac.slopedbe.auth.exception.JwtException;
import org.sesac.slopedbe.auth.service.LoginServiceImpl;
import org.sesac.slopedbe.auth.util.JwtUtil;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

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
		boolean shouldNotFilter = path.startsWith("/api/auth/") || path.startsWith("/api/facilities") || path.startsWith("/api/roads");
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

		try {
			final String authorizationHeader = request.getHeader("Authorization");
			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
				String token = authorizationHeader.substring(7);
				MemberCompositeKey compositeKey = jwtUtil.extractCompositeKey(token);
				log.info("Extracted composite key: {}, {}", compositeKey.getEmail(), compositeKey.getOauthType());

				authenticateUser(request, compositeKey);
				log.info("JWT authentication successful for path: {}", request.getRequestURI());
			} else {
				log.info("No valid Authorization header found for path: {}", request.getRequestURI());
			}
		} catch (JwtException e) {
			log.error("JWT Error for path {}: {}", request.getRequestURI(), e.getMessage());
		}

		chain.doFilter(request, response);
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