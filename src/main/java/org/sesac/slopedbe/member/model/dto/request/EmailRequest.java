package org.sesac.slopedbe.member.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EmailRequest(
	@NotBlank(message = "이메일를 입력해주세요")
	String email
){}
