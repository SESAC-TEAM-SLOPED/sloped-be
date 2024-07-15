package org.sesac.slopedbe.member.model.memberenum;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberOauthType {
	NAVER("TYPE_NAVER"),
	GOOGLE("TYPE_GOOGLE");

	private final String value;
}
