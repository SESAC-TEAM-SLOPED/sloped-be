package org.sesac.slopedbe.auth.service;

import java.util.Collection;
import java.util.List;

import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.member.exception.MemberErrorCode;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class LoginServiceImpl implements UserDetailsService {
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String compositeKey) throws UsernameNotFoundException {

		// compositeKey를 email과 oauthType으로 분리
		MemberCompositeKey key = parseCompositeKey(compositeKey);

		// email과 oauthType으로 사용자 검색
		Member member = memberRepository.findById(key)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
		return new GeneralUserDetails(member, getAuthorities(member), null);
	}


	private Collection<? extends GrantedAuthority> getAuthorities(Member member) {
		return List.of(new SimpleGrantedAuthority(member.getMemberRole().getValue()));
	}

	// 복합키 사용 + Spring 표준 UserDetailsService를 사용하기 때문에 해당 메서드 생성
	// compositeKey 생성 메서드
	public static String createCompositeKey(String email, MemberOauthType oauthType) {
		return email + "::" + oauthType.name();
	}

	// compositeKey 분리 메서드
	private static MemberCompositeKey parseCompositeKey(String compositeKey) {
		String[] parts = compositeKey.split("::");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Invalid compositeKey format");
		}
		return new MemberCompositeKey(parts[0], MemberOauthType.valueOf(parts[1]));
	}

	public boolean existsByEmailAndOauthType(String email, MemberOauthType oauthType) {
		return memberRepository.existsById(new MemberCompositeKey(email, oauthType));
	}



}
