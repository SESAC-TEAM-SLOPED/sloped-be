package org.sesac.slopedbe.member.service;

import org.sesac.slopedbe.member.model.dto.request.RegisterMemberRequest;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;

public interface MemberService {
    Member registerMember(RegisterMemberRequest registerMemberRequest);

    boolean checkDuplicateId(String id);

    String findIdByEmail(String email);

    void deleteMember(String email);

    Member updateMemberPassword(String id, String newPassword);

    Member updateMemberStatus(String email, MemberStatus status);

    Member updateMemberInfo(String email, String newNickname, String newPassword, boolean newDisability);
}
