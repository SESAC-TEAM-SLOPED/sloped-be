package org.sesac.slopedbe.auth.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationRequest {
	private String email;
	private String code;
	private String id;
	private String newPassword;
}
