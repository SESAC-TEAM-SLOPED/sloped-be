package org.sesac.slopedbe.member.service;

import java.util.Optional;

import org.sesac.slopedbe.member.exception.MemberErrorCode;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.dto.request.IdRequest;
import org.sesac.slopedbe.member.model.dto.request.RegisterMemberRequest;
import org.sesac.slopedbe.member.model.dto.request.RegisterSocialMemberRequest;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
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
		MemberOauthType oauthType = MemberOauthType.LOCAL;

		// email과 oauthType 기준, DB에 중복 검증
		if (memberRepository.findById(new MemberCompositeKey(email, oauthType)).isPresent()) {
			throw new MemberException(MemberErrorCode.MEMBER_EMAIL_ALREADY_EXISTS);
		}

		Member member = new Member(
			registerMemberRequest.memberId(),
			passwordEncoder.encode(registerMemberRequest.password()),
			registerMemberRequest.email(),
			registerMemberRequest.nickname(),
			registerMemberRequest.isDisabled(),
			oauthType
		);

		return memberRepository.save(member);
	}

	@Override
	public Member registerSocialMember(RegisterSocialMemberRequest registerSocialMemberRequest) {
		// 회원 가입 DB 저장
		String email = registerSocialMemberRequest.email();
		MemberOauthType oauthType = registerSocialMemberRequest.oauthType();

		// email과 oauthType 기준, DB에 중복 검증
		if (memberRepository.findById(new MemberCompositeKey(email, oauthType)).isPresent()) {
			throw new MemberException(MemberErrorCode.MEMBER_EMAIL_ALREADY_EXISTS);
		}

		Member member = new Member(
			registerSocialMemberRequest.email(),
			registerSocialMemberRequest.nickname(),
			registerSocialMemberRequest.isDisabled(),
			registerSocialMemberRequest.oauthType()
		);

		return memberRepository.save(member);
	}



	@Override
	public void checkDuplicateId(String memberId) {
		// MemberId DB에서 검색, 중복 여부 확인

		log.info("checkDuplicateId: {}", memberRepository.existsByMemberId(memberId));

		// 중복이면 errorCode 발생
		if(memberRepository.existsByMemberId(memberId)) {
			throw new MemberException(MemberErrorCode.MEMBER_ID_ALREADY_EXISTS);
		}
	}

	@Override
	public String findMemberIdByEmail(String email) {
		// email로 검색 후, MemberId 반환

		// MemberId 찾는 method로, Type은 Local로 지정
		MemberOauthType oauthType = MemberOauthType.LOCAL;

		MemberCompositeKey compositeKey = new MemberCompositeKey(email, oauthType);
		Member member = memberRepository.findById(compositeKey)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_ID_NOT_FOUND));
		return member.getMemberId();
	}

	@Override
	public void deleteMember(IdRequest idRequest) {
		// 마이페이지, 관리자 페이지 회원 정보 삭제

		String email = idRequest.email();
		MemberOauthType oauthType = idRequest.oauthType();

		MemberCompositeKey compositeKey = new MemberCompositeKey(email, oauthType);

		memberRepository.deleteById(compositeKey);
	}

	@Override
	public Member updateMemberPassword(String memberId, String newPassword) {
		// Local 유저, 비밀번호 찾기, 메일 인증 후, 비밀번호 변경
		MemberCompositeKey key = new MemberCompositeKey(memberId, MemberOauthType.LOCAL);

		Optional<Member> member = memberRepository.findById(key);

		if (member.isPresent()) {
			Member existingMember = member.get();
			existingMember.setPassword(passwordEncoder.encode(newPassword));
			return memberRepository.save(existingMember);
		} else {
			throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
		}
	}

	@Override
	public Member updateMemberStatus(IdRequest idRequest, MemberStatus status) {
		// 관리자 페이지, member status 변경

		String email = idRequest.email();
		MemberOauthType oauthType = idRequest.oauthType();

		Optional<Member> member = memberRepository.findById(new MemberCompositeKey(email, oauthType));
		if (member.isEmpty()) {
			throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
		}

		Member existingMember = member.get();
		existingMember.setMemberStatus(status);
		return memberRepository.save(existingMember);
	}

	@Override
	public Member updateMemberInfo(IdRequest idRequest, String newNickname, String newPassword, boolean newDisability) {
		//마이페이지, 회원정보 수정

		String email = idRequest.email();
		MemberOauthType oauthType = idRequest.oauthType();

		Optional<Member> member = memberRepository.findById(new MemberCompositeKey(email, oauthType));
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
