package org.sesac.slopedbe.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.sesac.slopedbe.auth.util.JwtUtil;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.dto.request.MemberRequest;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    private final JwtUtil jwtUtil;

    @Operation(summary = "회원 정보 수정", description = "마이페이지용, 요청된 회원 정보로 DB를 수정한다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "유저 정보 업데이트"),
        @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음.")
    })
    @PostMapping("/update-user")
    public ResponseEntity<Map<String, String>> updateMemberInfo(@RequestHeader("Authorization") String token, @RequestBody
    MemberRequest memberRequest) {
        String accessToken = token.substring(7);
        MemberCompositeKey compositeKey = jwtUtil.extractCompositeKey(accessToken);

        memberService.updateMemberInfo(compositeKey, memberRequest);
        Map<String, String> response = new HashMap<>();
        response.put("message", "유저 정보 업데이트");
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원 탈퇴", description = "마이페이지용, 요청된 회원 정보를 DB에서 삭제한다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
        @ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음.")
    })
    @DeleteMapping("/delete-user")
    public ResponseEntity<Map<String, String>> deleteMember(@RequestHeader("Authorization") String token) {
        String accessToken = token.substring(7);
        MemberCompositeKey compositeKey = jwtUtil.extractCompositeKey(accessToken);

        memberService.deleteMember(compositeKey);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 정지", description = "어드민: Member Status를 수정해 회원 기능을 정지시킨다.")
    @PostMapping("/blacklist")
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
