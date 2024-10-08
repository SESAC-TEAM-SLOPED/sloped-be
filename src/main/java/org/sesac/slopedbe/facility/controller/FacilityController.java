package org.sesac.slopedbe.facility.controller;

import java.net.URI;
import java.util.List;

import org.sesac.slopedbe.auth.util.JwtUtil;
import org.sesac.slopedbe.facility.exception.FacilityErrorCode;
import org.sesac.slopedbe.facility.exception.FacilityException;
import org.sesac.slopedbe.facility.model.dto.request.CreateFacilityRequest;
import org.sesac.slopedbe.facility.model.dto.response.CreateFacilityResponse;
import org.sesac.slopedbe.facility.model.dto.response.FacilityDetailResponse;
import org.sesac.slopedbe.facility.model.dto.response.FacilityResponse;
import org.sesac.slopedbe.facility.model.dto.response.FacilityScoreResponse;
import org.sesac.slopedbe.facility.model.dto.response.FacilitySimpleResponse;
import org.sesac.slopedbe.facility.model.entity.Facility;
import org.sesac.slopedbe.facility.service.FacilityService;
import org.sesac.slopedbe.facilityreview.model.dto.FacilityReviewResponseDTO;
import org.sesac.slopedbe.facilityreview.service.FacilityReviewService;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/facilities")
@RestController
@Tag(name = "facility-controller", description = "시설 API")
public class FacilityController {

    private final FacilityService facilityService;
    private final JwtUtil jwtUtil;
    private final FacilityReviewService facilityReviewService;

    @Operation(summary = "Facility 조회", description = "시설 ID로 시설을 간단 조회한다. (Bottom Sheet)")
    @ApiResponse(responseCode = "200", description = "시설 조회 성공",
        content = @Content(schema = @Schema(implementation = FacilityResponse.class)))
    @ApiResponse(responseCode = "404", description = "Facility not found")
    @GetMapping("/{id}")
    public ResponseEntity<FacilityResponse> getFacilityById(
        @Parameter(description = "시설 ID (필수)", required = true)
        @PathVariable(value = "id") Long id) {
        MemberCompositeKey memberCK = jwtUtil.getMemberCKFromHeader();
        return ResponseEntity.ok(facilityService.getFacility(id, memberCK));
    }

    @Operation(summary = "Facility 상세 조회", description = "시설 ID로 시설을 상세 조회한다.")
    @ApiResponse(responseCode = "200", description = "시설 상세 조회 성공",
        content = @Content(schema = @Schema(implementation = FacilityDetailResponse.class)))
    @ApiResponse(responseCode = "404", description = "Facility not found")
    @GetMapping("/{id}/detail")
    public ResponseEntity<FacilityDetailResponse> getFacilityDetailById(
        @Parameter(description = "시설 ID (필수)", required = true)
        @PathVariable(value = "id") Long id) {
        return ResponseEntity.ok(facilityService.getFacilityDetail(id));
    }

    @Operation(summary = "주변의 Facility 리스트 조회", description = "시설 ID로 주변 반경의 시설 리스트를 조회한다.")
    @ApiResponse(responseCode = "200", description = "주변 Facility 리스트 조회 성공",
        content = @Content(schema = @Schema(implementation = FacilityResponse.class)))
    @ApiResponse(responseCode = "400", description = "요청 시설 수가 너무 많습니다.")
    @GetMapping("")
    public ResponseEntity<List<FacilityResponse>> getNearbyFacilities(
        @Parameter(description = "현재 위치 Latitude (필수)", required = true) @RequestParam("latitude") double latitude,
        @Parameter(description = "현재 위치 Longitude (필수)", required = true) @RequestParam("longitude") double longitude,
        @Parameter(description = "반경 거리(단위: 미터) (필수)", required = true) @RequestParam("distance_meters") double distance_meters,
        @Parameter(description = "검색할 시설 개수 (필수)", required = true) @RequestParam("limit") int limit,
        @Parameter(description = "시설 타입(카테고리)") @RequestParam(value="type", required = false) String type
    ) {
        if (limit > 100) {
            throw new FacilityException(FacilityErrorCode.FIND_FACILITY_LIMIT_EXCEEDED);
        }

        return ResponseEntity.ok(facilityService.getNearbyFacilities(latitude, longitude, distance_meters, limit, type));
    }

    @Operation(summary = "이름으로 Facility 리스트 검색", description = "이름으로 검색한 Facility 리스트와 현재 위치로부터 거리 반환한다.")
    @ApiResponse(responseCode = "200", description = "Facility 리스트 검색 성공",
        content = @Content(schema = @Schema(implementation = FacilitySimpleResponse.class)))
    @GetMapping("/search")
    public ResponseEntity<List<FacilitySimpleResponse>> searchFacilities(
        @Parameter(description = "검색할 Facility 이름 (필수)", required = true) @RequestParam String name,
        @Parameter(description = "현재 위치 Latitude (필수)", required = true) @RequestParam Double latitude,
        @Parameter(description = "현재 위치 Longitude (필수)", required = true) @RequestParam Double longitude
    ) {
        return ResponseEntity.ok(facilityService.searchFacilities(name, latitude, longitude));
    }

    @Operation(summary = "시설의 모든 리뷰 확인", description = "시설에 작성된 리뷰 text, image를 확인한다.")
    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공")
    @ApiResponse(responseCode = "404", description = "작성한 리뷰가 없음")
    @GetMapping("/{facilityId}/get-facility-reviews")
    public ResponseEntity<List<FacilityReviewResponseDTO>> getFacilityReviews(
        @Parameter(description = "시설 ID (필수)", required = true) @PathVariable("facilityId") Long facilityId){
        List<FacilityReviewResponseDTO> reviews = facilityReviewService.readAllFacilityReviews(facilityId);
        if (reviews.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "시설의 리뷰 개수, 편리함 개수 확인", description = "시설에 작성된 모든 리뷰 개수, 편리함, 개수를 확인한다.")
    @ApiResponse(responseCode = "200", description = "리뷰 조회 성공")
    @ApiResponse(responseCode = "404", description = "작성한 리뷰가 없음")
    @GetMapping("/{facilityId}/get-facility-reviews-count")
    public ResponseEntity<List<Long>> getFacilityReviewsCount(
        @Parameter(description = "시설 ID (필수)", required = true) @PathVariable("facilityId") Long facilityId){
        List<Long> reviewCounts = facilityReviewService.findReviewCounts(facilityId);
        if (reviewCounts.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(reviewCounts);
    }


    @Operation(summary = "모든 Facility List 조회", description = "어드민: 모든 시설 리스트를 조회한다.")
    @ApiResponse(responseCode = "200", description = "시설 리스트 조회 성공",
        content = @Content(schema = @Schema(implementation = FacilityDetailResponse.class)))
    @GetMapping("/all")
    public ResponseEntity<Page<FacilityDetailResponse>> getAllFacilities(Pageable pageable) {
        return ResponseEntity.ok(facilityService.getAllFacilities(pageable));
    }

    @Operation(summary = "Facility 삭제", description = "시설 ID로 시설을 삭제한다.")
    @ApiResponse(responseCode = "204", description = "시설 삭제 성공")
    @ApiResponse(responseCode = "404", description = "시설이 존재하지 않습니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacility(
        @Parameter(description = "삭제할 Facility ID (필수)", required = true)
        @PathVariable Long id) {
        facilityService.deleteFacility(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Facility 등록", description = "시설 ID로 시설을 삭제한다.")
    @ApiResponse(responseCode = "201", description = "시설 등록 성공")
    @PostMapping
    public ResponseEntity<CreateFacilityResponse> createFacility(@RequestBody CreateFacilityRequest request) {
        Long facilityId = facilityService.saveFacility(new Facility(request));

        URI location = ServletUriComponentsBuilder
            .fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(facilityId)
            .toUri();

        return ResponseEntity.created(location).body(new CreateFacilityResponse(facilityId));
    }

    @Operation(summary = "Facility 추천 리스트 조회", description = "현재 위치 근처 접근성 점수가 높은 10개를 점수가 높은 순서대로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "시설 조회 성공")
    @GetMapping("/recommendation")
    public ResponseEntity<List<FacilityScoreResponse>> getFacilityListOrderByScores(
        @Parameter(description = "현재 위치 Latitude (필수)", required = true) @RequestParam("latitude") double latitude,
        @Parameter(description = "현재 위치 Longitude (필수)", required = true) @RequestParam("longitude") double longitude,
        @Parameter(description = "반경 거리(단위: 미터) (필수)", required = true) @RequestParam("distance_meters") double distance_meters
    ) {
        return ResponseEntity.ok(facilityService.getFacilityListOrderByScores(latitude, longitude, distance_meters));
    }

}