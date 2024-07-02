package org.sesac.slopedbe.member.model.memberenum;

public enum MemberOauthType {
	NAVER("TYPE_NAVER"),
	KAKAO("TYPE_KAKAO");

	MemberOauthType(String Value) {
		this.value = value;
	}
	private String value;
}
