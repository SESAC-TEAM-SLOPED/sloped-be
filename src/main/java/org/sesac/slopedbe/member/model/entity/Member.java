package org.sesac.slopedbe.member.model.entity;

import org.sesac.slopedbe.common.entity.BaseTimeEntity;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.model.type.MemberRole;
import org.sesac.slopedbe.member.model.type.MemberStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
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

    @EmbeddedId
    private MemberCompositeKey id;

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

    @Column(unique = true)
    private String memberId;

    @Column()
    private String password;

    @Column(length = 50)
    private String socialAuthCode;

    //Local member 생성자
    public Member(String memberId, String password, String email, String nickname, Boolean isDisabled, MemberOauthType memberOauthType ) {
        this.id = new MemberCompositeKey(email, memberOauthType);
        this.memberId = memberId;
        this.password = password;
        this.nickname = nickname;
        this.isDisability = isDisabled;
        this.memberRole = MemberRole.USER;
        this.memberStatus = MemberStatus.ACTIVE;
    }

    //Social member 생성자
    public Member(String email, String nickname, Boolean isDisabled, MemberOauthType memberOauthType) {
        this.id = new MemberCompositeKey(email, memberOauthType);
        this.nickname = nickname;
        this.isDisability = isDisabled;
        this.memberRole = MemberRole.USER;
        this.memberStatus = MemberStatus.ACTIVE;
    }
}
