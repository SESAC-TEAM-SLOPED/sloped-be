package org.sesac.slopedbe.member.controller;

import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;
import org.sesac.slopedbe.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

@RequestMapping("/api/users")
@RestController
@AllArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/duplicate-check")
    public ResponseEntity<Boolean> checkDuplicateEmail(@RequestParam String email) {
        boolean isDuplicated = memberService.checkDuplicateEmail(email);
        return ResponseEntity.ok(isDuplicated);
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

    @PostMapping("/register")
    public ResponseEntity<Member> register(@RequestBody Member member, @RequestParam String verifiedCode) {
        Member savedMember = memberService.registerMember(member, verifiedCode);
        return ResponseEntity.ok(savedMember);
    }

    @PutMapping("/request-reset")
    public ResponseEntity<Member> updatePassword(@RequestParam String email, @RequestParam String verifiedCode, @RequestParam String newPassword ){
        Member updatedMember = memberService.updateMemberPassword(email, verifiedCode, newPassword);
        return ResponseEntity.ok(updatedMember);
    }



}
