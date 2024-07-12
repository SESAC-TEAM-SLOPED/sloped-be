package org.sesac.slopedbe.auth.model.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailVerificationRequest {
	private String email;
	private String code;
	private String id;
}
