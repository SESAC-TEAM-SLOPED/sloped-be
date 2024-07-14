package org.sesac.slopedbe.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.memberenum.MemberRole;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(MemberServiceTest.TestConfig.class)
@ActiveProfiles("test")
public class MemberServiceTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberService memberService;

	@Autowired
	private PasswordEncoder passwordEncoder;

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
		testMember.setId("testId");
		testMember.setPassword(passwordEncoder.encode("plainTextPassword"));
		testMember.setDisability(false);
		memberRepository.save(testMember);
	}

	@Test
	public void testCheckDuplicateId() {
		boolean isDuplicated = memberService.checkDuplicateId("testId");
		assertTrue(isDuplicated);
	}

	@Test
	public void testDeleteMember() {
		Member newMember = new Member();
		newMember.setEmail("new@example.com");
		newMember.setNickname("김신입");
		newMember.setMemberStatus(MemberStatus.ACTIVE);
		newMember.setMemberRole(MemberRole.USER);
		newMember.setCreatedAt(LocalDateTime.now());
		newMember.setUpdatedAt(LocalDateTime.now());
		newMember.setPassword(passwordEncoder.encode("plainTextPassword"));
		memberRepository.save(newMember);

		memberService.deleteMember("new@example.com");

		Optional<Member> foundMember = memberRepository.findByEmail("new@example.com");
		assertTrue(foundMember.isEmpty());
	}

	@Test
	public void testUpdateMemberPassword() {
		String id = "testId";
		String newPassword = "newPassword123";

		Optional<Member> foundMember = memberRepository.findById(id);
		assertTrue(foundMember.isPresent());
		assertTrue(passwordEncoder.matches(newPassword, foundMember.get().getPassword()));
	}

	@Test
	public void testUpdateMemberStatus() {
		String email = "test@example.com";

		Optional<Member> foundMember = memberRepository.findByEmail(email);
		assertTrue(foundMember.isPresent());
		assertThat(foundMember.get().getMemberStatus()).isEqualTo(MemberStatus.BLOCKED);
	}

	@Test
	public void testUpdateMemberInfo() {
		String email = "test@example.com";
		String newNickname = "김갑돌";
		String newPassword = "newPassword123";
		boolean newDisability = true;

		Optional<Member> foundMember = memberRepository.findByEmail(email);
		assertTrue(foundMember.isPresent());
		assertThat(foundMember.get().getNickname()).isEqualTo(newNickname);
		assertTrue(passwordEncoder.matches(newPassword, foundMember.get().getPassword()));
		assertThat(foundMember.get().isDisability()).isEqualTo(newDisability);
	}

	@TestConfiguration
	static class TestConfig {

		@Bean
		public MemberService memberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
			return new MemberServiceImpl(memberRepository, passwordEncoder);
		}

		@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
	}
}
