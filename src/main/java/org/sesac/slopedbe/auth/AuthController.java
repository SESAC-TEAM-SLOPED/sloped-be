package org.sesac.slopedbe.auth;

import org.sesac.slopedbe.auth.DTO.AuthRequest;
import org.sesac.slopedbe.auth.DTO.AuthResponse;
import org.sesac.slopedbe.member.service.MemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

	private static final Logger log = LoggerFactory.getLogger(AuthController.class);
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;
	private final MemberService memberService;

	public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, MemberService memberService){
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.memberService = memberService;
	}

	@PostMapping(value="/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
		log.info("----------------------------------------------------------");
		log.info("Attempting to authenticate user: {}", authRequest.getId());

		try {
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getId(), authRequest.getPassword())
			);
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.info("User {} authenticated successfully", authRequest.getId());
		} catch (Exception e) {
			log.error("Authentication failed for user {}", authRequest.getId(), e);
			throw new Exception("INVALID_CREDENTIALS", e);
		}

		final UserDetails userDetails = memberService.loadUserByUsername(authRequest.getId());
		final String jwt = jwtUtil.generateToken(userDetails.getUsername());

		log.info("JWT generated for user {}: {}", authRequest.getId(), jwt);
		return ResponseEntity.ok(new AuthResponse(jwt));
	}
}
