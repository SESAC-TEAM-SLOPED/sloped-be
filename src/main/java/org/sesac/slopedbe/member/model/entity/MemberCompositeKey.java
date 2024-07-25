package org.sesac.slopedbe.member.model.entity;

import java.io.Serializable;

import org.sesac.slopedbe.member.model.type.MemberOauthType;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	@Enumerated(EnumType.STRING)
	private MemberOauthType oauthType;

}
