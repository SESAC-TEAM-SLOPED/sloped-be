package org.sesac.slopedbe.member.service;

import java.io.IOException;

import org.sesac.slopedbe.member.model.dto.request.MemberRequest;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberService {
    void registerMember(MemberRequest memberRequest);

    void registerSocialMember(MemberRequest memberRequest);

    void checkExistedId(String memberId);

    void checkDuplicateId(String memberId);

    String findMemberIdByEmail(String email);

    void deleteMember(String email, MemberOauthType oauthType);

    void updateMemberPassword(String memberId, String newPassword);

    void updateMemberStatus(MemberRequest memberRequest);

    Member updateMemberInfo(MemberRequest memberRequest);

    void sendSocialRegisterInformation(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException;
}
