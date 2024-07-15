package org.sesac.slopedbe.auth.service;

import java.util.ArrayList;

import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl implements UserDetailsService {
	private final MemberRepository memberRepository;

	public LoginServiceImpl(MemberRepository memberRepository){
		this.memberRepository = memberRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member = memberRepository.findByMemberId(username)
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		return new org.springframework.security.core.userdetails.User(member.getMemberId(), member.getPassword(), new ArrayList<>());
	}
}
