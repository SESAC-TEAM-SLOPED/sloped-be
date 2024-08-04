package org.sesac.slopedbe.config;

import java.util.Arrays;

import org.sesac.slopedbe.auth.filter.JwtRequestFilter;
import org.sesac.slopedbe.auth.handler.SocialAuthenticationFailureHandler;
import org.sesac.slopedbe.auth.handler.SocialAuthenticationSuccessHandler;
import org.sesac.slopedbe.auth.service.LoginServiceImpl;
import org.sesac.slopedbe.auth.util.JwtUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Slf4j
public class SecurityConfig {

	private final LoginServiceImpl memberService;
	private final JwtUtil jwtUtil;
	private final JwtRequestFilter jwtRequestFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http,
		SocialAuthenticationSuccessHandler socialAuthenticationSuccessHandler, SocialAuthenticationFailureHandler socialAuthenticationFailureHandler) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 비활성화
			.cors(Customizer.withDefaults())
			.cors(cors -> cors.configurationSource(corsConfigurationSource()))
			.authorizeHttpRequests(authorizeRequests ->
				authorizeRequests
					.requestMatchers("/**").permitAll()
					.requestMatchers("/api/roadReport/upload").authenticated()
					.anyRequest().authenticated()
			)
			.sessionManagement(sessionManagement ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.oauth2Login(oauth2 ->
				oauth2
					.successHandler(socialAuthenticationSuccessHandler)
					.failureHandler(socialAuthenticationFailureHandler)

			)
			.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public FilterRegistrationBean<JwtRequestFilter> jwtFilter() {
		FilterRegistrationBean<JwtRequestFilter> filter = new FilterRegistrationBean<>();
		filter.setFilter(new JwtRequestFilter(memberService, jwtUtil));
		filter.addUrlPatterns("/api/*");
		return filter;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L); // 1시간 동안 preflight 요청 결과를 캐시

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}


}

