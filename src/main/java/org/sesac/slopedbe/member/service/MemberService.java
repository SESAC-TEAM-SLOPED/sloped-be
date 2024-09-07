package org.sesac.slopedbe.member.service;

import lombok.RequiredArgsConstructor;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

}
