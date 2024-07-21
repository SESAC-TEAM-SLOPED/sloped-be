package org.sesac.slopedbe.member.service;

import java.util.Optional;

import org.sesac.slopedbe.member.exception.MemberErrorCode;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.dto.request.RegisterMemberRequest;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.type.MemberStatus;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@AllArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public Member registerMember(RegisterMemberRequest registerMemberRequest) {
		// 회원 가입 DB 저장
		String email = registerMemberRequest.email();

		// email 기준, DB에 중복 이메일 검증
		if (memberRepository.findByEmail(email).isPresent()) {
			throw new MemberException(MemberErrorCode.MEMBER_EMAIL_ALREADY_EXISTS);
		}

		return memberRepository.save(
			new Member(
				registerMemberRequest.id(),
				passwordEncoder.encode(registerMemberRequest.password()),
				registerMemberRequest.email(),
				registerMemberRequest.nickname(),
				registerMemberRequest.isDisabled()
		));

	}

	@Override
	public void checkDuplicateId(String memberId) {
		// Id DB에서 검색, 중복 여부 확인

		log.info("checkDuplicateId: {}", memberRepository.existsByMemberId(memberId));

		// 중복이면 errorCode 발생
		if(memberRepository.existsByMemberId(memberId)) {
			throw new MemberException(MemberErrorCode.MEMBER_ID_ALREADY_EXISTS);
		}
	}

	@Override
	public String findMemberIdByEmail(String email) {
		// email로 검색 후, MemberId 반환
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_ID_NOT_FOUND));
		return member.getMemberId();
	}

	@Override
	public void deleteMember(String email) {
		// DB에서 email 기준, 회원 정보 삭제
		memberRepository.deleteByEmail(email);
	}

	@Override
	public Member updateMemberPassword(String memberId, String newPassword) {
		//비밀번호 찾기, 메일 인증 후, 비밀번호 변경
		Optional<Member> member = memberRepository.findByMemberId(memberId);
		if (member.isPresent()) {
			Member existingMember = member.get();
			existingMember.setPassword(passwordEncoder.encode(newPassword));
			return memberRepository.save(existingMember);
		} else {
			throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
		}
	}

	@Override
	public Member updateMemberStatus(String email, MemberStatus status) {
		// 관리자 페이지, member status 변경
		Optional<Member> member = memberRepository.findByEmail(email);
		if (member.isEmpty()) {
			throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
		}

		Member existingMember = member.get();
		existingMember.setMemberStatus(status);
		return memberRepository.save(existingMember);
	}

	@Override
	public Member updateMemberInfo(String email, String newNickname, String newPassword, boolean newDisability) {
		//마이페이지, 회원정보 수정
		Optional<Member> member = memberRepository.findByEmail(email);
		if (member.isEmpty()) {
			throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
		}

		Member existingMember = member.get();
		existingMember.setNickname(newNickname);
		existingMember.setPassword(passwordEncoder.encode(newPassword));
		existingMember.setDisability(newDisability);
		return memberRepository.save(existingMember);
	}

}
