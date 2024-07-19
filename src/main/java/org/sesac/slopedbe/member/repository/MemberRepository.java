package org.sesac.slopedbe.member.repository;

import java.util.Optional;

import org.sesac.slopedbe.member.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Optional<Member> findByEmail(String email);
	Optional<Member> findByMemberId(String memberId);
	void deleteByEmail(String email);

	boolean existsByMemberId(String memberId);

	boolean existsByEmail(String email);
}
