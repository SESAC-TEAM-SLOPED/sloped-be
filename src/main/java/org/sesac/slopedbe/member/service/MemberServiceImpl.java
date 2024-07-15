package org.sesac.slopedbe.member.service;

import java.util.Optional;

import org.sesac.slopedbe.auth.exception.MemberAlreadyExistsException;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.memberenum.MemberRole;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Member registerMember(Member member) {
		String email = member.getEmail();

		if (memberRepository.findByEmail(email).isPresent()) {
			throw new MemberAlreadyExistsException("해당 이메일이 이미 존재합니다.");
		}

		member.setPassword(passwordEncoder.encode(member.getPassword()));
		member.setMemberStatus(MemberStatus.ACTIVE); // Default status
		member.setMemberRole(MemberRole.USER); // Default role

		return memberRepository.save(member);

	}

	@Override
	public boolean checkDuplicateId(String id) {
		//회원 가입 중복 확인 버튼 누를 때 사용
		return memberRepository.findById(id).isPresent();
	}

	@Override
	public String findIdByEmail(String email) {
		Optional<Member> member = memberRepository.findByEmail(email);
		if (member.isPresent()) {
			return member.get().getId().toString();
		} else {
			throw new IllegalArgumentException("Invalid email or verification code");
		}
	}

	@Override
	public void deleteMember(String email) {
		memberRepository.deleteByEmail(email);
	}

	@Override
	public Member updateMemberPassword(String id, String newPassword) {
		//비밀번호 모르는 경우, 인증번호 받아서 비밀번호 변경
		Optional<Member> member = memberRepository.findById(id);
		if (member.isPresent()) {
			Member existingMember = member.get();
			existingMember.setPassword(passwordEncoder.encode(newPassword));
			return memberRepository.save(existingMember);
		} else {
			throw new IllegalArgumentException("Invalid email or verification code");
		}
	}

	@Override
	public Member updateMemberStatus(String email, MemberStatus status) {
		Optional<Member> member = memberRepository.findByEmail(email);
		if (member.isPresent()) {
			Member existingMember = member.get();
			existingMember.setMemberStatus(status);
			return memberRepository.save(existingMember);
		} else {
			throw new IllegalArgumentException("Member not found");
		}
	}

	@Override
	public Member updateMemberInfo(String email, String newNickname, String newPassword, boolean newDisability) {
		//마이페이지에서 회원정보 수정
		Optional<Member> member = memberRepository.findByEmail(email);
		if (member.isPresent()) {
			Member existingMember = member.get();
			existingMember.setNickname(newNickname);
			existingMember.setPassword(passwordEncoder.encode(newPassword));
			existingMember.setDisability(newDisability);
			return memberRepository.save(existingMember);
		} else {
			throw new IllegalArgumentException("Member not found");
		}
	}

}
