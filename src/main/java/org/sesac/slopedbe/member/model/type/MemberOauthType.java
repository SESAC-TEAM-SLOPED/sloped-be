package org.sesac.slopedbe.member.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberOauthType {
	LOCAL("TYPE_LOCAL"),
	NAVER("TYPE_NAVER"),
	KAKAO("TYPE_KAKAO"),
	GOOGLE("TYPE_GOOGLE");

	private final String value;
}
