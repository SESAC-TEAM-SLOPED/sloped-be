package org.sesac.slopedbe.member.controller;

import org.sesac.slopedbe.auth.VerificationRequest;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;
import org.sesac.slopedbe.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@RequestMapping("/api/users")
@RestController
@AllArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/duplicate-check/id")
    public ResponseEntity<Boolean> checkDuplicateId(@RequestParam String id) {
        boolean isDuplicated = memberService.checkDuplicateId(id);
        return ResponseEntity.ok(isDuplicated);
    }

    @PostMapping("/find-id")
    public ResponseEntity<String> findIdByEmail(HttpServletRequest request, @RequestBody VerificationRequest verificationRequest) {
        String email = verificationRequest.getEmail();

        try {
            String id = memberService.findIdByEmail(email);
            HttpSession session = request.getSession();
            session.setAttribute("verifiedId", id);
            return ResponseEntity.ok(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid email or verification code");
        }
    }

    // // 세션에 저장된 ID 반환
    // @GetMapping("/get-verified-id")
    // public ResponseEntity<String> getVerifiedId(HttpServletRequest request) {
    //     HttpSession session = request.getSession();
    //     String id = (String) session.getAttribute("verifiedId");
    //     if (id != null) {
    //         return ResponseEntity.ok(id);
    //     } else {
    //         return ResponseEntity.badRequest().body("No verified ID found in session");
    //     }
    // }

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
    public ResponseEntity<Member> updatePassword(@RequestBody VerificationRequest verificationRequest ){
        String id = verificationRequest.getId();
        String newPassword = verificationRequest.getNewPassword();

        try {
            Member updatedMember = memberService.updateMemberPassword(id, newPassword);
            return ResponseEntity.ok(updatedMember);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
