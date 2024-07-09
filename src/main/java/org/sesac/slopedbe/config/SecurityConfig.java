package org.sesac.slopedbe.config;

import org.sesac.slopedbe.auth.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

	private JwtRequestFilter jwtRequestFilter;

	@Bean
	//테스트 과정에서 로그인 기능 삭제
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
			.authorizeRequests(authorizeRequests ->
				authorizeRequests
					.anyRequest().permitAll()  // 모든 요청에 대해 인증을 비활성화
			);
		return http.build();
	}

	// 인증 활성화
	// @Bean
	// public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	// 	http
	// 		.csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
	// 		.authorizeRequests(authorizeRequests ->
	// 			authorizeRequests
	// 				.anyRequest().authenticated()  // 모든 요청에 대해 인증을 요구
	// 		)
	// 		.sessionManagement(sessionManagement ->
	// 			sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 세션을 사용하지 않음
	// 		);
	//
	// 	http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);  // JWT 필터 추가
	//
	// 	return http.build();
	// }


	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
