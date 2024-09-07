package org.sesac.slopedbe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("함꼐가길 API Documentation")
				.version("1.0")
				.description("새싹 스프링부트 백엔드 과정 영등포 6기 (팀 경사났네) - 함께가길 프로젝트 API 문서"));
	}
}
