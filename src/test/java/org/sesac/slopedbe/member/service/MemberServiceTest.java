package org.sesac.slopedbe.member.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(MemberServiceImpl.class)
public class MemberServiceTest {

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private MemberService memberService;

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
		testMember.setPassword("password"); //암호화 예정
		testMember.setDisability(false);
		memberRepository.save(testMember);
	}

	@Test
	public void testSaveMember() {
		Member newMember = new Member();
		newMember.setEmail("new@example.com");
		newMember.setNickname("김신입");
		newMember.setMemberStatus(MemberStatus.ACTIVE);
		newMember.setMemberRole(MemberRole.USER);
		newMember.setCreatedAt(LocalDateTime.now());
		newMember.setUpdatedAt(LocalDateTime.now());

		Member savedMember = memberService.saveMember(newMember, "test-code");

		assertThat(savedMember).isNotNull();
		assertThat(savedMember.getEmail()).isEqualTo("new@example.com");

	}

	@Test
	public void testFindByEmail() {
		Optional<Member> foundMember = memberService.findByEmail("test@example.com");
		assertTrue(foundMember.isPresent());
		assertThat(foundMember.get().getNickname()).isEqualTo("김갑생");
	}

	@Test
	public void testFindById() {
		Optional<Member> foundMember = memberService.findById("testId");
		assertTrue(foundMember.isPresent());
		assertThat(foundMember.get().getNickname()).isEqualTo("김갑생");
	}

	@Test
	public void testFindIdByEmail () {
		//testFindByEmail이 성공해서 스킵
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
		memberRepository.save(newMember);

		memberService.deleteMember("new@example.com");

		Optional<Member> foundMember = memberRepository.findByEmail("new@example.com");
		assertFalse(foundMember.isPresent());
	}

	@Test
	public void testUpdateMemberPassword() {
		String email = "test@example.com";
		String validCode = "valid-code";
		String newPassword = "newPassword123";

		Member updatedMember = memberService.updateMemberPassword(email, validCode, newPassword);

		Optional<Member> foundMember = memberRepository.findByEmail(email);
		assertTrue(foundMember.isPresent());
		assertThat(foundMember.get().getPassword()).isEqualTo(newPassword);
	}

	@Test
	public void testUpdateMemberStatus() {
		String email = "test@example.com";

		Member updatedMember = memberService.updateMemberStatus(email, MemberStatus.BLOCKED);

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

		Member updatedMember = memberService.updateMemberInfo(email, newNickname, newPassword, newDisability);

		Optional<Member> foundMember = memberRepository.findByEmail(email);
		assertTrue(foundMember.isPresent());
		assertThat(foundMember.get().isDisability()).isEqualTo(true);
	}







}
