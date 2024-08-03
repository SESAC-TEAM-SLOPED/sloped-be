package org.sesac.slopedbe.member.abc;

import jakarta.validation.constraints.Email;

public record MemberSimpleRequest(
	String memberId,
	@Email(message = "이메일 형식이 아닙니다")
	String email) {
}
