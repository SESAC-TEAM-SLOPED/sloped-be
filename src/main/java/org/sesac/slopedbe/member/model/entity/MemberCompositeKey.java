package org.sesac.slopedbe.member.model.entity;

import java.io.Serializable;

import org.sesac.slopedbe.member.model.type.MemberOauthType;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MemberCompositeKey implements Serializable {

	private String email;
	private MemberOauthType oauthType;

}
