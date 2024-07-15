package org.sesac.slopedbe.auth.model.vo;

import lombok.Getter;

@Getter
public class MailVerificationRequest {
	private String email;
	private String code;
	private String id;
}
