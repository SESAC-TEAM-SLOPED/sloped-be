package org.sesac.slopedbe.auth.service;

import java.util.Collection;
import java.util.List;

import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.member.exception.MemberErrorCode;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LoginServiceImpl implements UserDetailsService {
	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		// memberId로 사용자 검색, 검색된 사용자, memberRole 반환

		Member member = memberRepository.findByMemberId(userId)
			.orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));
		return new GeneralUserDetails(member, getAuthorities(member), null); //Local user 검색, attribute는 null
	}

	private Collection<? extends GrantedAuthority> getAuthorities(Member member) {
		return List.of(new SimpleGrantedAuthority(member.getMemberRole().getValue()));
	}
}
