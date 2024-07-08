package org.sesac.slopedbe.member.service;

import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;

public interface MemberService {
    Member registerMember(Member member, String verifiedCode);
    boolean checkDuplicateId(String id);
    String findIdByEmail(String email);
    boolean checkPasswordByEmailAndId(String email, String id, String verifiedCode);
    void deleteMember(String email);
    Member updateMemberPassword(String id, String newPassword);
    Member updateMemberStatus(String email, MemberStatus status);
    Member updateMemberInfo(String email, String newNickname, String newPassword, boolean newDisability);
}
