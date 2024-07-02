package org.sesac.slopedbe.member.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "memberDefault")
@Entity
public class MemberDefault {
	@Id //기본 키
	@Column(nullable = false, unique = true, length = 20)
	@Email //이메일 주소 양식인지 검증
	private String email;

	@Column(nullable = false)
	private String id;

	private String password;



}