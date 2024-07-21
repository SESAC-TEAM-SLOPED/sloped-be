package org.sesac.slopedbe.member.controller;

import org.sesac.slopedbe.auth.model.CustomUserDetails;
import org.sesac.slopedbe.member.model.dto.request.CheckDuplicateIdRequest;
import org.sesac.slopedbe.member.model.dto.request.EmailRequest;
import org.sesac.slopedbe.member.model.dto.request.IdRequest;
import org.sesac.slopedbe.member.model.dto.request.RegisterMemberRequest;
import org.sesac.slopedbe.member.model.dto.request.UpdateRequest;
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
        // 회원 가입, 아이디 찾기 시 중복 아이디 검사 용도
        memberService.checkDuplicateId(checkDuplicateIdRequest.id());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("")
    public ResponseEntity<Member> updateMemberInfo(@RequestParam IdRequest idRequest, @RequestParam String newNickname, @RequestParam String newPassword, boolean newDisability) {
        // 마이페이지에서 회원 정보 수정 기능 용도
        Member updatedMember = memberService.updateMemberInfo(idRequest, newNickname, newPassword, newDisability);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteMember(@RequestParam IdRequest idRequest) {
        // 마이 페이지, 회원 탈퇴 용도
        memberService.deleteMember(idRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/blacklist")
    public ResponseEntity<Member> updateStatus(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam IdRequest idRequest, @RequestParam MemberStatus status) {
        // 관리자 페이지, Status를 수정해 회원 정지 용도
        log.info("User {} updated status of member {} to {}", userDetails.getUsername(), idRequest, status);

        Member updatedMember = memberService.updateMemberStatus(idRequest, status);
        return ResponseEntity.ok(updatedMember);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterMemberResponse> register(@Valid @RequestBody RegisterMemberRequest request) {
        // 회원 가입
        Member savedMember = memberService.registerMember(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new RegisterMemberResponse(savedMember.getEmail()));
    }

    @PostMapping("/find-id")
    public ResponseEntity<String> findMemberIdByEmail(@Valid @RequestBody EmailRequest emailRequest) {
        // 아이디 찾기
        String email = emailRequest.email();
        String memberId = memberService.findMemberIdByEmail(email);
        return ResponseEntity.ok(memberId);
    }

    @PutMapping("/request-reset")
    public ResponseEntity<Member> updatePassword(@RequestBody UpdateRequest updateRequest){
        // 비밀 번호 찾기, 비밀 번호 재설정 method
        Member updatedMember = memberService.updateMemberPassword(updateRequest.getMemberId(), updateRequest.getNewPassword());
        return ResponseEntity.ok(updatedMember);
    }
}
