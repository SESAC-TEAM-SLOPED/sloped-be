package org.sesac.slopedbe.common.config;

import org.sesac.slopedbe.auth.filter.JwtRequestFilter;
import org.sesac.slopedbe.auth.handler.CustomAuthenticationFailureHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig  {

	private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

	private final JwtRequestFilter jwtRequestFilter;
	private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

	public SecurityConfig(JwtRequestFilter jwtRequestFilter, CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
		this.jwtRequestFilter = jwtRequestFilter;
		this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())  // CSRF 보호 비활성화
			.authorizeRequests(authorizeRequests ->
				authorizeRequests
					.requestMatchers("/login", "/error").permitAll()
					.anyRequest().authenticated()  // 모든 요청에 대해 인증을 요구
			)
			.sessionManagement(sessionManagement ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 세션을 사용하지 않음
			);

		http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);  // JWT 필터 추가

		if (jwtRequestFilter != null) {
			http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
		} else {
			log.error("jwtRequestFilter is null");
			throw new IllegalStateException("JwtRequestFilter cannot be null");
		}

		// Form login configuration
		http.formLogin(formLogin -> formLogin
			.loginPage("/login/local")
			.permitAll()
			.defaultSuccessUrl("/", true)
			.failureHandler(customAuthenticationFailureHandler)
		);

		return http.build();
	}


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws  Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

}
