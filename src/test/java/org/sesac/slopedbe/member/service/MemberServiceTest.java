package org.sesac.slopedbe.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.dto.request.IdRequest;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.model.type.MemberRole;
import org.sesac.slopedbe.member.model.type.MemberStatus;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@EnableJpaAuditing
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
		testMember = new Member(
			"testId",
			passwordEncoder.encode("plainTextPassword"),
			"test@example.com",
			"김갑생",
			false
		);
		testMember.setOauthType(MemberOauthType.LOCAL);
		memberRepository.save(testMember);
	}

	@Test
	public void testCheckDuplicateId() {
		assertThrows(MemberException.class, () -> memberService.checkDuplicateId("testId"));
	}

	@Test
	public void testDeleteMember() {
		Member newMember = new Member();
		newMember.setEmail("new@example.com");
		newMember.setNickname("김신입");
		newMember.setMemberStatus(MemberStatus.ACTIVE);
		newMember.setMemberRole(MemberRole.USER);
		newMember.setPassword(passwordEncoder.encode("plainTextPassword"));
		newMember.setOauthType(MemberOauthType.LOCAL);
		memberRepository.save(newMember);

		IdRequest idRequest = new IdRequest("new@example.com", MemberOauthType.LOCAL);
		memberService.deleteMember(idRequest);

		Optional<Member> foundMember = memberRepository.findById(new MemberCompositeKey("new@example.com", MemberOauthType.LOCAL));
		assertTrue(foundMember.isEmpty());
	}

	@Test
	public void testUpdateMemberPassword() {
		String id = "testId";
		String newPassword = "newPassword123";

		memberService.updateMemberPassword(id, newPassword);

		Optional<Member> foundMember = memberRepository.findByMemberId(id);
		assertTrue(foundMember.isPresent());
		assertTrue(passwordEncoder.matches(newPassword, foundMember.get().getPassword()));
	}

	@Test
	public void testUpdateMemberStatus() {
		IdRequest idRequest = new IdRequest("test@example.com", MemberOauthType.LOCAL);
		memberService.updateMemberStatus(idRequest, MemberStatus.BLOCKED);

		Optional<Member> foundMember = memberRepository.findById(new MemberCompositeKey("test@example.com", MemberOauthType.LOCAL));
		assertTrue(foundMember.isPresent());
		assertThat(foundMember.get().getMemberStatus()).isEqualTo(MemberStatus.BLOCKED);
	}

	@Test
	public void testUpdateMemberInfo() {
		IdRequest idRequest = new IdRequest("test@example.com", MemberOauthType.LOCAL);
		String newNickname = "김갑돌";
		String newPassword = "newPassword123";
		boolean newDisability = true;

		memberService.updateMemberInfo(idRequest, newNickname, newPassword, newDisability);

		Optional<Member> foundMember = memberRepository.findById(new MemberCompositeKey("test@example.com", MemberOauthType.LOCAL));
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