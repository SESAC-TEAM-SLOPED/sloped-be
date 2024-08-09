package org.sesac.slopedbe.member.repository;

import java.util.Optional;

import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MemberRepository extends JpaRepository<Member, MemberCompositeKey> {

	Optional<Member> findById(MemberCompositeKey id);
	Optional<Member> findByMemberId(String memberId);
	boolean existsByMemberId(String memberId);

	@Modifying
	@Transactional
	@Query(value = "UPDATE member SET member_status = :status WHERE email = :email AND oauth_type = :oauthType", nativeQuery = true)
	void updateMemberStatusToWithdrawn(@Param("email") String email, @Param("oauthType") String oauthType, @Param("status") String status);

	// 아래 메서드들은 JpaRepository에 이미 존재.
	// void deleteById(MemberCompositeKey memberCompositeKey);
	// boolean existsById(MemberCompositeKey memberCompositeKey);
	// Optional<Member> findById(MemberCompositeKey memberCompositeKey);
}
