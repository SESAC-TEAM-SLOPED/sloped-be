package org.sesac.slopedbe.member.model.vo.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterMemberRequest (
	@NotBlank(message = "아이디를 입력해주세요")
	String id,
	@NotBlank(message = "비밀번호를 입력해주세요")
	String password,
	@NotBlank(message = "이메일을 입력해주세요")
	@Email(message = "이메일 형식이 아닙니다")
	String email,
	@NotBlank(message = "닉네임을 입력해주세요")
	String nickname,
	@NotNull(message = "장애 여부를 입력해주세요")
	Boolean isDisabled
){}
