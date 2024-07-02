package org.sesac.slopedbe.member.model.memberenum;

import lombok.Getter;

@Getter
public enum MemberStatus {
	ACTIVE("STATUS_ACTIVE"), //활동 가능 유저
	BLOCKED("STATUS_BLOCKED"); //블락 유저

	MemberStatus(String value){
		this.value = value;
	}
	private String value;
}
