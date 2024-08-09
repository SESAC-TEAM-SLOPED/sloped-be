package org.sesac.slopedbe.road.controller;

import java.util.List;

import org.sesac.slopedbe.road.model.dto.RoadMarkerInfoDTO;
import org.sesac.slopedbe.road.service.RoadService;
import org.sesac.slopedbe.roadreport.exception.RoadReportErrorCode;
import org.sesac.slopedbe.roadreport.exception.RoadReportException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/roads")
@RestController
public class RoadController {

    private final RoadService roadService;
    @Operation(summary = "승인된 도로 전체 조회", description = "통행 불편 제보에서 승인된 모든 도로 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "승인된 도로 정보 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/get-points")
    public ResponseEntity<List<RoadMarkerInfoDTO>> getApprovedRoadPoints(
        @Parameter(description = "현재 위치 Latitude (필수)", required = true) @RequestParam("latitude") double latitude,
        @Parameter(description = "현재 위치 Longitude (필수)", required = true) @RequestParam("longitude") double longitude,
        @Parameter(description = "반경 거리(단위: 미터) (필수)", required = true) @RequestParam("distance_meters") double distance_meters,
        @Parameter(description = "검색할 도로 제보 개수 (필수)", required = true) @RequestParam("limit") int limit
    ) {
        List<RoadMarkerInfoDTO> approvedPoints = roadService.getApprovedRoadPoints(latitude, longitude, distance_meters, limit);

        if (limit > 100) {
            throw new RoadReportException(RoadReportErrorCode.ROAD_REPORT_LIMIT_EXCEEDED);
        }
        return ResponseEntity.ok(approvedPoints);
    }


    @Operation(summary = "도로 정보 조회", description = "특정 도로 ID로 도로의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "도로 정보를 성공적으로 조회했습니다."),
        @ApiResponse(responseCode = "404", description = "도로를 찾을 수 없습니다.")
    })
    @GetMapping("/{roadId}")
    public RoadMarkerInfoDTO getRoadInfo(@Parameter(description = "조회할 도로의 ID", required = true)
    @PathVariable(name = "roadId") Long roadId) {
        RoadMarkerInfoDTO roadDTO = roadService.findByRoaId(roadId);
        return roadDTO;
    }
}
