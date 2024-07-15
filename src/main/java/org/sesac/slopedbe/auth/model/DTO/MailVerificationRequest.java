package org.sesac.slopedbe.auth.model.DTO;

import lombok.Getter;

@Getter
public class MailVerificationRequest {
	private String email;
	private String code;
	private String id;
}
