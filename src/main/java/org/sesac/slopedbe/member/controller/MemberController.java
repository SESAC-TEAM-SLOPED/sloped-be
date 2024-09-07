package org.sesac.slopedbe.member.controller;

import lombok.RequiredArgsConstructor;
import org.sesac.slopedbe.member.service.MemberService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class MemberController {

    private final MemberService memberService;

}
