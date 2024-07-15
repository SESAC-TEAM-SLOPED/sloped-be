package org.sesac.slopedbe.member.service;

import java.util.Optional;

import org.sesac.slopedbe.member.exception.MemberErrorCode;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.dto.request.RegisterMemberRequest;
import org.sesac.slopedbe.member.model.entity.Member;
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
	public Member registerMember(RegisterMemberRequest registerMemberRequest) {
		String email = registerMemberRequest.email();

		if (memberRepository.findByEmail(email).isPresent()) {
			throw new MemberException(MemberErrorCode.MEMBER_ALREADY_EXISTS);
		}

		return memberRepository.save(new Member(
				registerMemberRequest.userId(),
				passwordEncoder.encode(registerMemberRequest.password()),
				registerMemberRequest.email(),
				registerMemberRequest.nickname(),
				registerMemberRequest.isDisabled()
		));

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
