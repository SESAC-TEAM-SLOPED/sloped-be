package org.sesac.slopedbe.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.dto.request.MemberRequest;
import org.sesac.slopedbe.member.model.dto.request.MemberSimpleRequest;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/users")
@RestController
@AllArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "중복 아이디 검사", description = "회원 가입용, 동일 아이디가 존재하는 지 검사한다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "MemberId가 중복되지 않습니다."),
        @ApiResponse(responseCode = "409", description = "MemberId가 중복됩니다.")
    })
    @PostMapping("/duplicate-check/id")
    public ResponseEntity<Map<String, String>> checkDuplicateId(@RequestBody MemberSimpleRequest memberRequest) {
        Map<String, String> response = new HashMap<>();

        try {
            String memberId = memberRequest.memberId();
            memberService.checkDuplicateId(memberId);
            response.put("message", "MemberId가 중복되지 않습니다.");
            return ResponseEntity.ok(response);
        } catch (MemberException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
    }

    @Operation(summary = "중복 아이디 검사", description = "아이디 찾기용, 중복 아이디가 존재하는 지 검사한다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "MemberId가 존재합니다."),
        @ApiResponse(responseCode = "404", description = "MemberId가 존재하지 않습니다.")
    })
    @PostMapping("/duplicate-check/find-id")
    public ResponseEntity<Map<String, String>> checkExistedId(@RequestBody MemberSimpleRequest memberRequest) {
        Map<String, String> response = new HashMap<>();

        try {
            String memberId = memberRequest.memberId();
            memberService.checkExistedId(memberId);
            response.put("message", "MemberId가 존재합니다.");
            return ResponseEntity.ok(response);
        } catch (MemberException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "회원 가입", description = "로컬 유저 정보를 DB에 저장한다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "로컬 유저 가입 성공")
    })
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> registerLocalMember(@RequestBody MemberRequest memberRequest) {

        memberService.registerMember(memberRequest);
        Map<String, String> response = new HashMap<>();
        response.put("message", "로컬 유저 가입 성공");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "아이디 찾기", description = "로컬 유저 아이디를 반환한다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "아이디 찾기 성공")
    })
    @PostMapping("/find-id")
    public ResponseEntity<Map<String, String>> findMemberIdByEmail(@RequestBody MemberSimpleRequest memberRequest) {

        String email = memberRequest.email();
        String memberId = memberService.findMemberIdByEmail(email);
        Map<String, String> response = new HashMap<>();
        response.put("memberId", memberId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 찾기", description = "로컬 유저 비밀번호를 재설정한다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "비밀번호 재설정 성공")
    })
    @PutMapping("/request-reset")
    public ResponseEntity<Map<String, String>> updatePassword(@RequestBody MemberRequest memberRequest){
        Map<String, String> response = new HashMap<>();
        try {
            memberService.updateMemberPassword(memberRequest.memberId(), memberRequest.password());
            response.put("message", "비밀번호 재설정 성공");
            return ResponseEntity.ok(response);
        } catch (MemberException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @Operation(summary = "회원 가입", description = "소셜 유저 정보를 DB에 저장한다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "소셜 유저 가입 성공")
    })
    @PostMapping("/register/social")
    public ResponseEntity<Map<String, String>> registerSocialMember(@RequestBody MemberRequest memberRequest) {
        memberService.registerSocialMember(memberRequest);
        Map<String, String> response = new HashMap<>();
        response.put("message", "소셜 유저 가입 성공");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "회원 정보 수정", description = "마이페이지용, 요청된 회원 정보로 DB를 수정한다.")
    @PutMapping("")
    public ResponseEntity<Member> updateMemberInfo(@RequestBody MemberRequest memberRequest) {
        // 경로 업데이트 예정!
        Member updatedMember = memberService.updateMemberInfo(memberRequest);
        return ResponseEntity.ok(updatedMember);
    }

    @Operation(summary = "회원 탈퇴", description = "마이페이지용, 요청된 회원 정보를 DB에서 삭제한다.")
    @DeleteMapping("")
    public ResponseEntity<Void> deleteMember(@RequestBody MemberRequest memberRequest) {
        // 경로 업데이트 예정!
        String email = memberRequest.email();
        MemberOauthType oauthType = memberRequest.oauthType();

        memberService.deleteMember(email, oauthType);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 정지", description = "어드민: Member Status를 수정해 회원 기능을 정지시킨다.")
    @PutMapping("/blacklist")
    public ResponseEntity<Map<String, String>> updateStatus(@RequestBody MemberRequest memberRequest) {
        // 경로 업데이트 예정!
        Map<String, String> response = new HashMap<>();

        try {
            memberService.updateMemberStatus(memberRequest);
            response.put("message", "Member Status를 수정 성공");
            return ResponseEntity.ok(response);
        } catch (MemberException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
