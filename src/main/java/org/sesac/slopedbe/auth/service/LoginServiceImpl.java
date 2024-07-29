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
		MemberCompositeKey key = parseCompositeKey(compositeKey);

		Member member = memberRepository.findById(key)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
		return new GeneralUserDetails(member, getAuthorities(member), null);
	}

	private Collection<? extends GrantedAuthority> getAuthorities(Member member) {
		return List.of(new SimpleGrantedAuthority(member.getMemberRole().getValue()));
	}

	public static String createCompositeKey(String email, MemberOauthType oauthType) {
		return email + "::" + oauthType.name();
	}

	private static MemberCompositeKey parseCompositeKey(String compositeKey) {
		String[] parts = compositeKey.split("::");
		if (parts.length != 2) {
			throw new IllegalArgumentException("Invalid compositeKey format");
		}
		return new MemberCompositeKey(parts[0], MemberOauthType.valueOf(parts[1]));
	}

}
