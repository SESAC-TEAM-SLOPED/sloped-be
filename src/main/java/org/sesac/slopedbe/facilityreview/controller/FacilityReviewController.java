package org.sesac.slopedbe.facilityreview.controller;

import java.util.List;

import org.sesac.slopedbe.auth.util.JwtUtil;
import org.sesac.slopedbe.facilityreview.model.dto.FacilityReviewRequestDTO;
import org.sesac.slopedbe.facilityreview.model.dto.FacilityReviewResponseDTO;
import org.sesac.slopedbe.facilityreview.service.FacilityReviewService;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@RestController
public class FacilityReviewController {

    private final FacilityReviewService facilityReviewService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "시설 리뷰 등록", description = "시설에 대한 리뷰를 등록한다.")
    @ApiResponse(responseCode = "201", description = "리뷰 등록 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 또는 시설")
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    @PostMapping("/{facilityId}/create-reviews")
    public ResponseEntity<Void> createFacilityReview(@RequestHeader("Authorization") String token,
        @Parameter(description = "시설 ID (필수)", required = true) @PathVariable("facilityId") Long facilityId,
        FacilityReviewRequestDTO facilityReviewRequestDTO) {
        String accessToken = token.substring(7);
        MemberCompositeKey compositeKey = jwtUtil.extractCompositeKey(accessToken);

        facilityReviewService.createFacilityReview(compositeKey, facilityId, facilityReviewRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "유저가 작성한 모든 리뷰 확인", description = "마이페이지용, 본인이 작성한 모든 리뷰 text, image를 확인한다.")
    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공")
    @ApiResponse(responseCode = "404", description = "작성한 리뷰가 없음")
    @GetMapping("/get-all-reviews")
    public ResponseEntity<List<FacilityReviewResponseDTO>> readUserFacilityReviews(@RequestHeader("Authorization") String token){
        String accessToken = token.substring(7);
        MemberCompositeKey compositeKey = jwtUtil.extractCompositeKey(accessToken);

        List<FacilityReviewResponseDTO> reviews = facilityReviewService.readAllUserFacilityReviews(compositeKey);
        if (reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "작성 리뷰 내용 수정", description = "마이페이지용, 본인이 작성한 리뷰 내용을 수정한다.")
    @ApiResponse(responseCode = "204", description = "리뷰 수정 성공")
    @PutMapping("/{facilityReviewId}/update-review")
    public ResponseEntity<Void> updateFacilityReview(
        @Parameter(description = "시설 리뷰 ID (필수)", required = true) @PathVariable("facilityReviewId") Long facilityReviewId,
        FacilityReviewRequestDTO facilityReviewRequestDTO){
        facilityReviewService.updateFacilityReview(facilityReviewId, facilityReviewRequestDTO);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "작성 리뷰 내용 삭제", description = "마이페이지용, 본인이 작성한 리뷰 내용을 삭제한다.")
    @ApiResponse(responseCode = "204", description = "리뷰 삭제 성공")
    @DeleteMapping("/{facilityReviewId}")
    public ResponseEntity<Void> deleteFacilityReview(
        @Parameter(description = "시설 리뷰 ID (필수)", required = true) @PathVariable("facilityReviewId") Long facilityReviewId){
        facilityReviewService.deleteFacilityReview(facilityReviewId);

        return ResponseEntity.noContent().build();
    }













}
