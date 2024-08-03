package org.sesac.slopedbe.member.abc;

import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.model.type.MemberStatus;

import jakarta.validation.constraints.Email;

public record MemberRequest (
	String memberId,
	String password,
	@Email(message = "이메일 형식이 아닙니다")
	String email,
	String nickname,
	Boolean isDisabled,
	MemberOauthType oauthType,
	MemberStatus status
){}
