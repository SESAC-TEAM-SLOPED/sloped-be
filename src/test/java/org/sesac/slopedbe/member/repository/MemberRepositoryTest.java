package org.sesac.slopedbe.member.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.memberenum.MemberRole;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class MemberRepositoryTest {

	@Autowired
	private MemberRepository memberRepository;

	private Member testMember;

	@BeforeEach
	public void setUp() {
		testMember = new Member();
		testMember.setEmail("test@example.com");
		testMember.setNickname("김갑생");
		testMember.setMemberStatus(MemberStatus.ACTIVE);
		testMember.setMemberRole(MemberRole.ADMIN);
		testMember.setCreatedAt(LocalDateTime.now());
		testMember.setUpdatedAt(LocalDateTime.now());
		memberRepository.save(testMember);
	}

	@Test
	public void testFindByEmail(){
		Optional<Member> foundMember = memberRepository.findByEmail("test@example.com");
		assertThat(foundMember).isPresent();
		assertThat(foundMember.get().getNickname()).isEqualTo("김갑생");
	}

	@Test
	public void testSaveMember() {
		Member newMember = new Member();
		newMember.setEmail("newTest@example.com");
		newMember.setNickname("newTestUser");
		newMember.setMemberStatus(MemberStatus.ACTIVE);
		newMember.setMemberRole(MemberRole.USER);
		newMember.setCreatedAt(LocalDateTime.now());
		newMember.setUpdatedAt(LocalDateTime.now());
		memberRepository.save(newMember);

		Optional<Member> foundMember = memberRepository.findByEmail("newTest@example.com");
		assertThat(foundMember).isPresent();
		assertThat(foundMember.get().getNickname()).isEqualTo(newMember.getNickname());
	}

	@Test
	public void testDeleteMember() {
		memberRepository.deleteByEmail("test@example.com");
		Optional<Member> foundMember = memberRepository.findByEmail("test@example.com");
		assertThat(foundMember).isNotPresent();
	}
}
