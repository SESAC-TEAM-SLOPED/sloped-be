package org.sesac.slopedbe.member.controller;

import org.sesac.slopedbe.auth.exception.MemberAlreadyExistsException;
import org.sesac.slopedbe.auth.model.vo.MailVerificationRequest;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;
import org.sesac.slopedbe.member.model.vo.UpdateRequest;
import org.sesac.slopedbe.member.model.vo.request.CheckDuplicateIdRequest;
import org.sesac.slopedbe.member.model.vo.request.RegisterMemberRequest;
import org.sesac.slopedbe.member.model.vo.response.RegisterMemberResponse;
import org.sesac.slopedbe.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

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

        try {
            String id = memberService.findIdByEmail(email);
            return ResponseEntity.ok(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid email or verification code");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMemberInfo(@RequestParam String email, @RequestParam String newNickname, @RequestParam String newPassword, boolean newDisability) {
        Member updatedMember = memberService.updateMemberInfo(email, newNickname, newPassword, newDisability);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@RequestParam String email) {
        memberService.deleteMember(email);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/blacklist")
    public ResponseEntity<Member> updateStatus(@RequestParam String email, @RequestParam MemberStatus status) {
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
        String id = updateRequest.getId();
        String newPassword = updateRequest.getNewPassword();

        try {
            Member updatedMember = memberService.updateMemberPassword(id, newPassword);
            return ResponseEntity.ok(updatedMember);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @ExceptionHandler(MemberAlreadyExistsException.class)
    public ResponseEntity<String> handleMemberAlreadyExistsException(MemberAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

}
