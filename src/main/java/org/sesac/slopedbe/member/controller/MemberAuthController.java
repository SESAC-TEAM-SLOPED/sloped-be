package org.sesac.slopedbe.member.controller;

import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class MemberAuthController {

	@Autowired
	private final MemberService memberService;

	@PostMapping("/signup")
	public ResponseEntity<Member> signUp(@RequestBody Member member, @RequestParam String verifiedCode) {
		Member savedMember = memberService.saveMember(member, verifiedCode);
		return ResponseEntity.ok(savedMember);
	}

	@PutMapping("/request-reset")
	public ResponseEntity<Member> updatePassword(@RequestParam String email, @RequestParam String verifiedCode, @RequestParam String newPassword ){
		Member updatedMember = memberService.updateMemberPassword(email, verifiedCode, newPassword);
		return ResponseEntity.ok(updatedMember);
	}

}
