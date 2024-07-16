package org.sesac.slopedbe.member.controller;

import org.sesac.slopedbe.auth.model.CustomUserDetails;
import org.sesac.slopedbe.auth.model.dto.MailVerificationRequest;
import org.sesac.slopedbe.member.model.dto.UpdateRequest;
import org.sesac.slopedbe.member.model.dto.request.CheckDuplicateIdRequest;
import org.sesac.slopedbe.member.model.dto.request.RegisterMemberRequest;
import org.sesac.slopedbe.member.model.dto.response.RegisterMemberResponse;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.type.MemberStatus;
import org.sesac.slopedbe.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/users")
@RestController
@AllArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/duplicate-check/id")
    public ResponseEntity<String> checkDuplicateId(@Valid @RequestBody CheckDuplicateIdRequest checkDuplicateIdRequest) {
        memberService.checkDuplicateId(checkDuplicateIdRequest.id());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/find-id")
    public ResponseEntity<String> findIdByEmail(@Valid @RequestBody MailVerificationRequest mailVerificationRequest) {
        String email = mailVerificationRequest.email();
        String id = memberService.findIdByEmail(email);
        return ResponseEntity.ok(id);
    }

    @PutMapping("")
    public ResponseEntity<Member> updateMemberInfo(@RequestParam String email, @RequestParam String newNickname, @RequestParam String newPassword, boolean newDisability) {
        Member updatedMember = memberService.updateMemberInfo(email, newNickname, newPassword, newDisability);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteMember(@RequestParam String email) {
        memberService.deleteMember(email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/blacklist")
    public ResponseEntity<Member> updateStatus(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String email, @RequestParam MemberStatus status) {
        log.info("User {} updated status of member {} to {}", userDetails.getUsername(), email, status);

        Member updatedMember = memberService.updateMemberStatus(email, status);
        return ResponseEntity.ok(updatedMember);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterMemberResponse> register(@Valid @RequestBody RegisterMemberRequest request) {
        Member savedMember = memberService.registerMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterMemberResponse(savedMember.getEmail()));
    }

    @PutMapping("/request-reset")
    public ResponseEntity<Member> updatePassword(@RequestBody UpdateRequest updateRequest){
        Member updatedMember = memberService.updateMemberPassword(updateRequest.getId(), updateRequest.getNewPassword());
        return ResponseEntity.ok(updatedMember);
    }
}
