package org.sesac.slopedbe.member.repository;

import java.util.Optional;

import org.sesac.slopedbe.member.model.entity.Member;
import org.springframework.data.repository.CrudRepository;

public interface MemberRepository extends CrudRepository<Member, Long> {
	Optional<Member> findByEmail(String email);
	Optional<Member> findById(String id);
	void deleteByEmail(String email);
}
