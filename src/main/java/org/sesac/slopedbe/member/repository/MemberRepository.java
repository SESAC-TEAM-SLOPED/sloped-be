package org.sesac.slopedbe.member.repository;

import java.util.Optional;

import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, MemberCompositeKey> {

	Optional<Member> findById(MemberCompositeKey id);
	Optional<Member> findByMemberId(String memberId);
	boolean existsByMemberId(String memberId);


	// 아래 메서드들은 JpaRepository에 이미 존재.
	// void deleteById(MemberCompositeKey memberCompositeKey);
	// boolean existsById(MemberCompositeKey memberCompositeKey);
	// Optional<Member> findById(MemberCompositeKey memberCompositeKey);
}
