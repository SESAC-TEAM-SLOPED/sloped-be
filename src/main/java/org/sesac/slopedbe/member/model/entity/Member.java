package org.sesac.slopedbe.member.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.sesac.slopedbe.common.entity.BaseTimeEntity;
import org.sesac.slopedbe.member.model.memberenum.MemberOauthType;
import org.sesac.slopedbe.member.model.memberenum.MemberRole;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter // 테스트용
@NoArgsConstructor
@Table(name = "member")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Member extends BaseTimeEntity {

    @Id //기본 키
    @Column(nullable = false, unique = true, length = 200)
    @Email //이메일 주소 양식인지 검증
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
    private String id; // 로컬 계정 Login ID

    @Column(length = 255)
    private String password; // 로컬 계정 Login Password

    @Column(length = 50)
    private String socialAuthCode; // 소셜 로그인 Auth Code

    @Column(length = 50)
    private String socialOauthType; // 소셜 로그인 OAuth Type

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
