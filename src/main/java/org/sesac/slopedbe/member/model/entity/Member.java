package org.sesac.slopedbe.member.model.entity;

import java.time.LocalDateTime;

import org.sesac.slopedbe.common.entity.BaseTimeEntity;
import org.sesac.slopedbe.member.model.memberenum.MemberOauthType;
import org.sesac.slopedbe.member.model.memberenum.MemberRole;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter // 테스트용
@NoArgsConstructor
@Table(name = "member")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Member extends BaseTimeEntity {

    @Id //기본 키
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

    @Column(length = 255)
    private String refreshToken;

    @Enumerated(EnumType.STRING)
    private MemberOauthType oauthType;

    @Column(length = 255)
    private String id;

    @Column(length = 255)
    private String password; // 로컬 계정 Login Password //암호화 예정

    @Column(length = 50)
    private String socialAuthCode;

    @Column(length = 50)
    private String socialOauthType;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
