package org.sesac.slopedbe.member.service;

import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MemberService extends UserDetailsService {
    Member registerMember(Member member);

    boolean checkDuplicateId(String id);

    String findIdByEmail(String email);

    void deleteMember(String email);

    Member updateMemberPassword(String id, String newPassword);

    Member updateMemberStatus(String email, MemberStatus status);

    Member updateMemberInfo(String email, String newNickname, String newPassword, boolean newDisability);
}
