package org.sesac.slopedbe.member.service;

import org.sesac.slopedbe.member.model.dto.request.MemberRequest;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;

public interface MemberService {
    void registerMember(MemberRequest memberRequest);

    void registerSocialMember(MemberRequest memberRequest);

    void checkExistedId(String memberId);

    void checkDuplicateId(String memberId);

    String findMemberIdByEmail(String email);

    void deleteMember(String email, MemberOauthType oauthType);

    void updateMemberPassword(String memberId, String newPassword);

    void updateMemberStatus(MemberRequest memberRequest);

    void updateMemberInfo(MemberCompositeKey memberCompositeKey, MemberRequest memberRequest);
}
