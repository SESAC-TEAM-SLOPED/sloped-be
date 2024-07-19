package org.sesac.slopedbe.auth.model.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SocialLoginInfoDTO {
	private String email;
	private String socialAuthCode;
	private String socialOauthType;
	private Map<String, Object> attributes;
}
