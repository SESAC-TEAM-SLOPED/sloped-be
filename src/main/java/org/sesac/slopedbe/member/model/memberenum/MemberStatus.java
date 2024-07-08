package org.sesac.slopedbe.member.model.memberenum;

import lombok.Getter;

@Getter
public enum MemberStatus {
	ACTIVE("STATUS_ACTIVE"),
	BLOCKED("STATUS_BLOCKED");

	MemberStatus(String value){
		this.value = value;
	}
	private String value;
}
