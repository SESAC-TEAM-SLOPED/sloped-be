package org.sesac.slopedbe.member.model.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRequest {
	private String email;
	private String code;
	private String id;
	private String newPassword;
}

