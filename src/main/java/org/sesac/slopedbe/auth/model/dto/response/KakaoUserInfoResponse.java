package org.sesac.slopedbe.auth.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoUserInfoResponse {

	//카카오 계정 정보
	@JsonProperty("kakao_account")
	public KakaoAccount kakaoAccount;

	@Getter
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class KakaoAccount {

		//카카오계정 대표 이메일
		@JsonProperty("email")
		private String email;

	}

}
