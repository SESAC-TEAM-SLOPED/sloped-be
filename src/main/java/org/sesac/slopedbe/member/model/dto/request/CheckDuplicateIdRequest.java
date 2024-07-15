package org.sesac.slopedbe.member.model.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CheckDuplicateIdRequest (
	@NotBlank(message = "아이디를 입력해주세요")
	String id
){
}
