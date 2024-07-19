package org.sesac.slopedbe.config;

import org.sesac.slopedbe.auth.filter.JwtRequestFilter;
import org.sesac.slopedbe.auth.service.LoginServiceImpl;
import org.sesac.slopedbe.auth.util.JwtUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@Slf4j
public class SecurityConfig {

	private final LoginServiceImpl memberService;
	private final JwtUtil jwtUtil;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 비활성화
			.authorizeHttpRequests(authorizeRequests ->
				authorizeRequests
					.requestMatchers("/joinpage", "/login/**",  "/api/auth/**","/api/users/**"
					,"/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**", "/webjars/**").permitAll()
					.anyRequest().authenticated()
			)
			.sessionManagement(sessionManagement ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.oauth2Login(oauth2 ->
				oauth2
					.loginPage("http://localhost:3000/joinpage")
					.defaultSuccessUrl("http://localhost:3000/")
					.failureUrl("http://localhost:3000/login?error=true")
					// .successHandler(successHandler())
			)
		;

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

	// @Bean
	// //테스트용!!
	// public SimpleUrlAuthenticationSuccessHandler successHandler() {
	// 	SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
	// 	log.info("소셜 로그인 성공!");
	// 	handler.setDefaultTargetUrl("http://localhost:3000/joinpage");  // 성공 후 리다이렉트 URL 설정
	// 	return handler;
	// }



}

