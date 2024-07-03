package org.sesac.slopedbe.member.service;

import java.util.Optional;

import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;

public interface MemberService {
    Member saveMember(Member member, String verifiedCode); // 회원 가입
    Optional<Member> findByEmail(String email); // 이메일 중복 체크
    Optional<Member> findById(String id); // 아이디 중복 체크
    String findIdByEmail(String email, String verifiedCode); //아이디 찾기
    Member updateMemberPassword(String email, String verifiedCode, String newPassword); // 비밀번호 찾기(재설정)
    void deleteMember(String email); // 회원 탈퇴, 회원 삭제
    Member updateMemberStatus(String email, MemberStatus status); // 회원 블락
    Member updateMemberInfo(String email, Member updatedInfo); // 회원 정보 수정
}
