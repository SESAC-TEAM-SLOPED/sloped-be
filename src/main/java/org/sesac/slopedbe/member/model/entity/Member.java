package org.sesac.slopedbe.member.model.entity;

import org.sesac.slopedbe.common.entity.BaseTimeEntity;
import org.sesac.slopedbe.member.model.memberenum.MemberOauthType;
import org.sesac.slopedbe.member.model.memberenum.MemberRole;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "member")
@Entity
public class Member extends BaseTimeEntity {

    @Id
    @Column(nullable = false, unique = true, length = 200)
    @Email
    private String email;

    @Column(nullable = false, length = 20)
    private String nickname;

    private boolean isDisability;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberStatus memberStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberRole memberRole;

    @Column()
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private MemberOauthType oauthType;

    @Column(unique = true)
    private String memberId;

    @Column()
    private String password;

    @Column(length = 50)
    private String socialAuthCode;

    @Column(length = 50)
    private String socialOauthType;

    public Member(String memberId, String password, String email, String nickname, Boolean isDisabled) {
        this.memberId = memberId;
        this.password = password;
        this.email = email;
        this.nickname = nickname;
        this.isDisability = isDisabled;
        this.memberRole = MemberRole.USER;
        this.memberStatus = MemberStatus.ACTIVE;
    }
}
