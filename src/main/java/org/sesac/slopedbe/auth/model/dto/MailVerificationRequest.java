package org.sesac.slopedbe.auth.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MailVerificationRequest (
	@NotBlank(message = "이메일을 입력해주세요")
	@Email(message = "이메일 형식이 올바르지 않습니다")
	String email,
	@NotBlank(message = "인증 코드를 입력해주세요")
	String code,
	@NotBlank(message = "아이디를 입력해주세요")
	String id) {
}
