package org.sesac.slopedbe.member.service;

import java.util.Optional;

import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService{

	private final MemberRepository memberRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	@Override
	public Member registerMember(Member member, String verifiedCode) {

		if (isValidCode(verifiedCode)) {
			if (member.getPassword() != null) {
				member.setPassword(passwordEncoder.encode(member.getPassword()));
			}
			return memberRepository.save(member);
		} else {
			throw new IllegalArgumentException("Invalid verification code");
		}
	}

	@Override
	public boolean checkDuplicateEmail(String email) {
		//다시 구현
		return memberRepository.findByEmail(email).isPresent();
	}

	@Override
	public boolean checkDuplicateId(String id) {
		//다시 구현
		return memberRepository.findById(id).isPresent();
	}

	@Override
	public String findIdByEmail(String email, String verifiedCode) {
		Optional<Member> member = memberRepository.findByEmail(email);
		if (member.isPresent() && isValidCode(verifiedCode)) {
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
	public Member updateMemberPassword(String email, String verifiedCode, String newPassword) {
		//비밀번호 모르는 경우, 인증번호 받아서 비밀번호 변경
		Optional<Member> member = memberRepository.findByEmail(email);
		if (member.isPresent() && isValidCode(verifiedCode)) {
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
			existingMember.setPassword(newPassword);
			existingMember.setDisability(newDisability);
			return memberRepository.save(existingMember);
		} else {
			throw new IllegalArgumentException("Member not found");
		}
	}

	private boolean isValidCode(String code) {
		// 인증 코드 확인 로직 작성 예정!
		// 예시: Redis에서 인증 코드를 조회하여 유효성을 검사
		return true; // 실제 로직으로 대체 필요
	}


}
