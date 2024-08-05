package org.sesac.slopedbe.auth.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserInfoResponse {

	// API 호출 결과 코드
	@JsonProperty("resultcode")
	private String resultCode;

	// 호출 결과 메시지
	@JsonProperty("message")
	private String message;

	// 사용자 정보
	@JsonProperty("response")
	public Response response;

	@Getter
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class Response {

		// 동일인 식별 정보
		@JsonProperty("id")
		private String id;

		// 사용자 별명
		@JsonProperty("nickname")
		private String nickname;

		// 사용자 이름
		@JsonProperty("name")
		private String name;

		// 사용자 메일 주소
		@JsonProperty("email")
		private String email;

		// 성별 (F: 여성, M: 남성, U: 확인불가)
		@JsonProperty("gender")
		private String gender;

		// 사용자 연령대
		@JsonProperty("age")
		private String age;

		// 사용자 생일 (MM-DD 형식)
		@JsonProperty("birthday")
		private String birthday;

		// 사용자 프로필 사진 URL
		@JsonProperty("profile_image")
		private String profileImage;

		// 출생연도
		@JsonProperty("birthyear")
		private String birthYear;

		// 휴대전화번호
		@JsonProperty("mobile")
		private String mobile;
	}
}
