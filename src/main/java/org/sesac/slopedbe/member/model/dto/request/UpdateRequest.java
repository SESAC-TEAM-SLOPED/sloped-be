package org.sesac.slopedbe.member.model.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRequest {
	private String email;
	private String memberId;
	private String newPassword;
}

