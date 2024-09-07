package org.sesac.slopedbe.member.repository;

import org.sesac.slopedbe.member.model.entity.Member;
import org.springframework.data.repository.CrudRepository;

public interface MemberRepository extends CrudRepository<Member, Long> {
}
