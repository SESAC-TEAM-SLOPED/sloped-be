package org.sesac.slopedbe.member.model.dto.request;

import org.sesac.slopedbe.member.model.type.MemberOauthType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterSocialMemberRequest(
	@NotBlank(message = "닉네임을 입력해주세요")
	String nickname,
	@NotNull(message = "장애 여부를 입력해주세요")
	Boolean isDisabled,

	String email,
	MemberOauthType oauthType
){}
