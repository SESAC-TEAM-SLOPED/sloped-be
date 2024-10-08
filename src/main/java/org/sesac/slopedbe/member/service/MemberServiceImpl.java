package org.sesac.slopedbe.member.service;

import java.util.Optional;

import org.sesac.slopedbe.member.exception.MemberErrorCode;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.dto.request.MemberRequest;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
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
	public void checkDuplicateId(String memberId) {
		if (memberRepository.existsByMemberId(memberId)) {
			throw new MemberException(MemberErrorCode.MEMBER_ID_ALREADY_EXISTS);
		}
	}

	@Override
	public void checkExistedId(String memberId) {
		if(!memberRepository.existsByMemberId(memberId)) {
			throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
		}
	}

	@Override
	public void registerMember(MemberRequest memberRequest) {
		String email = memberRequest.email();
		MemberOauthType oauthType = MemberOauthType.LOCAL;

		if (memberRepository.findById(new MemberCompositeKey(email, oauthType)).isPresent()) {
			throw new MemberException(MemberErrorCode.MEMBER_EMAIL_ALREADY_EXISTS);
		}

		Member member = new Member(
			memberRequest.memberId(),
			passwordEncoder.encode(memberRequest.password()),
			memberRequest.email(),
			memberRequest.nickname(),
			memberRequest.isDisabled(),
			oauthType
		);
		memberRepository.save(member);
	}

	@Override
	public void registerSocialMember(MemberRequest memberRequest) {
		String email = memberRequest.email();
		MemberOauthType oauthType = memberRequest.oauthType();

		if (memberRepository.findById(new MemberCompositeKey(email, oauthType)).isPresent()) {
			throw new MemberException(MemberErrorCode.MEMBER_EMAIL_ALREADY_EXISTS);
		}

		Member member = new Member(
			memberRequest.email(),
			memberRequest.nickname(),
			memberRequest.isDisabled(),
			memberRequest.oauthType()
		);
		memberRepository.save(member);
	}

	@Override
	public String findMemberIdByEmail(String email) {
		MemberOauthType oauthType = MemberOauthType.LOCAL;
		MemberCompositeKey compositeKey = new MemberCompositeKey(email, oauthType);

		Member member = memberRepository.findById(compositeKey)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_ID_NOT_FOUND));
		return member.getMemberId();
	}

	@Override
	public void deleteMember(MemberCompositeKey memberCompositeKey) {
		Optional<Member> member = memberRepository.findById(memberCompositeKey);
		if (member.isEmpty()) {
			throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
		}
		memberRepository.deleteById(memberCompositeKey);
	}

	@Override
	public void updateMemberPassword(String memberId, String newPassword) {
		Optional<Member> member = memberRepository.findByMemberId(memberId);

		if (member.isPresent()) {
			Member existingMember = member.get();
			existingMember.setPassword(passwordEncoder.encode(newPassword));
			memberRepository.save(existingMember);
		} else {
			throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
		}
	}

	@Override
	public void updateMemberStatus(MemberRequest memberRequest) {
		String email = memberRequest.email();
		MemberOauthType oauthType = memberRequest.oauthType();

		Optional<Member> member = memberRepository.findById(new MemberCompositeKey(email, oauthType));
		if (member.isEmpty()) {
			throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
		}

		Member existingMember = member.get();
		existingMember.setMemberStatus(memberRequest.status());
		memberRepository.save(existingMember);
	}

	@Override
	public void updateMemberInfo(MemberCompositeKey memberCompositeKey, MemberRequest memberRequest) {
		Optional<Member> member = memberRepository.findById(memberCompositeKey);
		if (member.isEmpty()) {
			throw new MemberException(MemberErrorCode.MEMBER_NOT_FOUND);
		}

		Member existingMember = member.get();
		if (memberRequest.nickname() != null) {
			existingMember.setNickname(memberRequest.nickname());
		}
		if (memberRequest.password() != null) {
			existingMember.setPassword(passwordEncoder.encode(memberRequest.password()));
		}
		existingMember.setDisability(memberRequest.isDisabled());
		memberRepository.save(existingMember);
	}
}
