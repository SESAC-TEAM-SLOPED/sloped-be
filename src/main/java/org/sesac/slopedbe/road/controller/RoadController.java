package org.sesac.slopedbe.road.controller;

import java.util.List;

import org.sesac.slopedbe.road.model.dto.RoadMarkerInfoDTO;
import org.sesac.slopedbe.road.service.RoadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<List<RoadMarkerInfoDTO>> getApprovedRoadPoints() {
        List<RoadMarkerInfoDTO> approvedPoints = roadService.getApprovedRoadPoints();
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
