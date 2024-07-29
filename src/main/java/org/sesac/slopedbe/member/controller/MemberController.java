package org.sesac.slopedbe.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.sesac.slopedbe.auth.model.GeneralUserDetails;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.dto.request.CheckDuplicateIdRequest;
import org.sesac.slopedbe.member.model.dto.request.EmailRequest;
import org.sesac.slopedbe.member.model.dto.request.IdRequest;
import org.sesac.slopedbe.member.model.dto.request.RegisterMemberRequest;
import org.sesac.slopedbe.member.model.dto.request.RegisterSocialMemberRequest;
import org.sesac.slopedbe.member.model.dto.request.UpdateRequest;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/users")
@RestController
@AllArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "중복 아이디 검사", description = "회원 가입용 중복 아이디 검사 용도")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No content, ID is not duplicated"),
        @ApiResponse(responseCode = "409", description = "Conflict, ID is duplicated")
    })
    @PostMapping("/duplicate-check/id")
    public ResponseEntity<Map<String, String>> checkDuplicateId(@Valid @RequestBody CheckDuplicateIdRequest checkDuplicateIdRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            memberService.checkDuplicateId(checkDuplicateIdRequest.id());
            response.put("message", "ID is not duplicated");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
        } catch (MemberException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @Operation(summary = "중복 아이디 검사", description = "아이디 찾기용 중복 아이디 검사 용도")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "No content, ID is not duplicated"),
        @ApiResponse(responseCode = "409", description = "Conflict, ID is duplicated")
    })
    @PostMapping("/duplicate-check/find-id")
    public ResponseEntity<Map<String, String>> checkExistedId(@Valid @RequestBody CheckDuplicateIdRequest checkDuplicateIdRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            memberService.checkExistedId(checkDuplicateIdRequest.id());
            response.put("message", "ID exists");
            return ResponseEntity.ok(response);
        } catch (MemberException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "회원 가입", description = "로컬 유저 회원 가입")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created, member registered"),
        @ApiResponse(responseCode = "409", description = "Conflict, email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterMemberRequest request) {

        Member savedMember = memberService.registerMember(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Member registered");
        response.put("email", savedMember.getId().getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "아이디 찾기", description = "로컬 유저 아이디 찾기")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK, ID found"),
        @ApiResponse(responseCode = "404", description = "Not found, email does not exist")
    })
    @PostMapping("/find-id")
    public ResponseEntity<Map<String, String>> findMemberIdByEmail(@Valid @RequestBody EmailRequest emailRequest) {

        String email = emailRequest.email();
        String memberId = memberService.findMemberIdByEmail(email);
        Map<String, String> response = new HashMap<>();
        response.put("memberId", memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 찾기", description = "로컬 유저 비밀번호 재설정")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK, password updated"),
        @ApiResponse(responseCode = "404", description = "Not found, member does not exist")
    })
    @PutMapping("/request-reset")
    public ResponseEntity<Map<String, String>> updatePassword(@RequestBody UpdateRequest updateRequest){
        Map<String, String> response = new HashMap<>();
        try {
            Member updatedMember = memberService.updateMemberPassword(updateRequest.getMemberId(), updateRequest.getNewPassword());
            response.put("message", "Password updated");
            response.put("member", updatedMember.toString());
            return ResponseEntity.ok(response);
        } catch (MemberException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "회원 가입", description = "소셜 유저 회원 가입")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created, social member registered"),
        @ApiResponse(responseCode = "409", description = "Conflict, email already exists")
    })
    @PostMapping("/register/social")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterSocialMemberRequest request) {
        Member savedMember = memberService.registerSocialMember(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Social member registered");
        response.put("email", savedMember.getId().getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "회원 정보 수정", description = "마이페이지에서 회원 정보 수정")
    @PutMapping("")
    public ResponseEntity<Member> updateMemberInfo(@RequestParam IdRequest idRequest, @RequestParam String newNickname, @RequestParam String newPassword, boolean newDisability) {
        // 업데이트 예정!
        Member updatedMember = memberService.updateMemberInfo(idRequest, newNickname, newPassword, newDisability);
        return ResponseEntity.ok(updatedMember);
    }

    @Operation(summary = "회원 탈퇴", description = "마이페이지, 회원 탈퇴")
    @DeleteMapping("")
    public ResponseEntity<Void> deleteMember(@RequestParam IdRequest idRequest) {
        // 업데이트 예정!
        memberService.deleteMember(idRequest);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 정지", description = "어드민: Member Status를 수정해 회원 정지")
    @PutMapping("/blacklist")
    public ResponseEntity<Member> updateStatus(@AuthenticationPrincipal GeneralUserDetails userDetails, @RequestParam IdRequest idRequest, @RequestParam MemberStatus status) {
        // 업데이트 예정!
        log.info("User {} updated status of member {} to {}", userDetails.getUsername(), idRequest, status);

        Member updatedMember = memberService.updateMemberStatus(idRequest, status);
        return ResponseEntity.ok(updatedMember);
    }
}
