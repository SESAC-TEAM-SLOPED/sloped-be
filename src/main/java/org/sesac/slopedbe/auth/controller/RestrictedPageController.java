package org.sesac.slopedbe.auth.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RestrictedPageController {

	@GetMapping("/mypage-request")
	public void accessMypage(HttpServletResponse response) throws IOException {
		//JWT Filter 적용 목적
		log.info("mypage access 요청");
		ResponseEntity.ok("Access granted");
	}
}
