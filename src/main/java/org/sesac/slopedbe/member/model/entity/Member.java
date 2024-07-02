package org.sesac.slopedbe.member.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sesac.slopedbe.common.entity.BaseTimeEntity;
import org.sesac.slopedbe.member.model.memberenum.MemberOauthType;
import org.sesac.slopedbe.member.model.memberenum.MemberRole;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;

import jakarta.validation.constraints.Email;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Entity
public class Member extends BaseTimeEntity {
    @Id //기본 키
    @Column(nullable = false, length = 20)
    @Email //이메일 주소 양식인지 검증
    private String email;

    @Column(nullable = false, length = 10)
    private String nickname;

    private boolean isDisability;

    @Column(nullable = false, updatable = false)
    private String createdAt;

    private String updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus memberStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole memberRole;

    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private MemberOauthType oauthType;

}
