package org.sesac.slopedbe.auth.model.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverUserInfoResponse {

	@JsonProperty("resultcode")
	private String resultCode; // API 호출 결과 코드
	@JsonProperty("message")
	private String message; // 호출 결과 메시지
	@JsonProperty("response")
	public Response response; // 사용자 정보

	@Getter
	@NoArgsConstructor
	@JsonIgnoreProperties(ignoreUnknown = true)
	public class Response {

		@JsonProperty("id")
		private String id; // 동일인 식별 정보
		@JsonProperty("nickname")
		private String nickname; // 사용자 별명
		@JsonProperty("name")
		private String name; // 사용자 이름
		@JsonProperty("email")
		private String email; // 사용자 메일 주소
		@JsonProperty("gender")
		private String gender; // 성별 (F: 여성, M: 남성, U: 확인불가)
		@JsonProperty("age")
		private String age; // 사용자 연령대
		@JsonProperty("birthday")
		private String birthday; // 사용자 생일 (MM-DD 형식)
		@JsonProperty("profile_image")
		private String profileImage; // 사용자 프로필 사진 URL
		@JsonProperty("birthyear")
		private String birthYear; // 출생연도
		@JsonProperty("mobile")
		private String mobile; // 휴대전화번호
	}
}
