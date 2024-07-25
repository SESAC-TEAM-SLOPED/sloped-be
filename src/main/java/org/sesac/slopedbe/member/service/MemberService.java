package org.sesac.slopedbe.member.service;

import java.io.IOException;

import org.sesac.slopedbe.member.model.dto.request.IdRequest;
import org.sesac.slopedbe.member.model.dto.request.RegisterMemberRequest;
import org.sesac.slopedbe.member.model.dto.request.RegisterSocialMemberRequest;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.type.MemberStatus;
import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberService {
    Member registerMember(RegisterMemberRequest registerMemberRequest);

    Member registerSocialMember(RegisterSocialMemberRequest registerSocialMemberRequest);

    void checkExistedId(String memberId);

    void checkDuplicateId(String memberId);

    String findMemberIdByEmail(String email);

    void deleteMember(IdRequest idRequest);

    Member updateMemberPassword(String memberId, String newPassword);

    Member updateMemberStatus(IdRequest idRequest, MemberStatus status);

    Member updateMemberInfo(IdRequest idRequest, String newNickname, String newPassword, boolean newDisability);

    void sendSocialRegisterInformation(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException;
}
