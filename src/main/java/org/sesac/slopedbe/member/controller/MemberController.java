package org.sesac.slopedbe.member.controller;

import java.util.Optional;

import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;
import org.sesac.slopedbe.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class MemberController {

    @Autowired
    private final MemberService memberService;

    @GetMapping("/duplicate-check")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        Optional<Member> member = memberService.findByEmail(email);
        return ResponseEntity.ok(member.isPresent());
    }

    @PutMapping("/:id")
    public ResponseEntity<Member> updateInfo(@RequestParam String email, @RequestParam String newNickname, @RequestParam String newPassword, boolean newDisability) {
        Member updatedMember = memberService.updateMemberInfo(email, newNickname, newPassword, newDisability);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/:id")
    public ResponseEntity<Void> deleteMember (@RequestParam String email){
        memberService.deleteMember(email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/:id/blacklist")
    public ResponseEntity<Member> updateStatus(@RequestParam String email, @RequestParam MemberStatus status) {
        Member updatedMember = memberService.updateMemberStatus(email, status);
        return ResponseEntity.ok(updatedMember);
    }



}
