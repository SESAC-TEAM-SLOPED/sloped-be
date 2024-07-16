package org.sesac.slopedbe.member.model.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberStatus {
	ACTIVE("STATUS_ACTIVE"),
	BLOCKED("STATUS_BLOCKED");

	private final String value;
}
