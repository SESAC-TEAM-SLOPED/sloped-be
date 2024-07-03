package org.sesac.slopedbe.member.repository;

import java.util.Optional;

import org.sesac.slopedbe.member.model.entity.Member;
import org.springframework.data.repository.CrudRepository;

public interface MemberRepository extends CrudRepository<Member, Long> {
	Optional<Member> findByEmail(String email); //이메일 중복 확인, 회원 삭제 용도
	Optional<Member> findById(String id); // 아이디 중복 체크, 아이디 찾기 용도
	void deleteByEmail(String email); // 회원 삭제
}
