package org.sesac.slopedbe.member.service;

import org.sesac.slopedbe.member.model.dto.request.IdRequest;
import org.sesac.slopedbe.member.model.dto.request.RegisterMemberRequest;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.type.MemberStatus;

public interface MemberService {
    Member registerMember(RegisterMemberRequest registerMemberRequest);

    void checkDuplicateId(String memberId);

    String findMemberIdByEmail(String email);

    void deleteMember(IdRequest idRequest);

    Member updateMemberPassword(String memberId, String newPassword);

    Member updateMemberStatus(IdRequest idRequest, MemberStatus status);

    Member updateMemberInfo(IdRequest idRequest, String newNickname, String newPassword, boolean newDisability);
}
