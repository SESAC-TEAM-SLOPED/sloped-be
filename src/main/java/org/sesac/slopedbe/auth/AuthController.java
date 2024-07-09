package org.sesac.slopedbe.auth;

import org.sesac.slopedbe.auth.DTO.AuthRequest;
import org.sesac.slopedbe.auth.DTO.AuthResponse;
import org.sesac.slopedbe.member.service.MemberService;
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

	private AuthenticationManager authenticationManager;

	private JwtUtil jwtUtil;

	private MemberService memberService;

	@PostMapping("/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthRequest authRequest) throws Exception {
		try {
			Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
			);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}

		final UserDetails userDetails = memberService.loadUserByUsername(authRequest.getUsername());
		final String jwt = jwtUtil.generateToken(userDetails.getUsername());

		return ResponseEntity.ok(new AuthResponse(jwt));
	}
}
