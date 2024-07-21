package org.sesac.slopedbe.member.model.dto.request;

import org.sesac.slopedbe.member.model.type.MemberOauthType;

import jakarta.validation.constraints.NotBlank;

public record IdRequest(
	@NotBlank(message = "이메일를 입력해주세요")
	String email,
	@NotBlank(message = "회원 타입을 입력해주세요")
	MemberOauthType oauthType
){}
