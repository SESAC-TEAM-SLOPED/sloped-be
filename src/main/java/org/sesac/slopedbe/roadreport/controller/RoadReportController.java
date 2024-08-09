package org.sesac.slopedbe.roadreport.controller;

import java.util.List;
import java.util.Optional;

import org.sesac.slopedbe.auth.util.JwtUtil;
import org.sesac.slopedbe.common.type.AddressMapping;
import org.sesac.slopedbe.gpt.service.GPTService;
import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.road.model.dto.RoadMarkerInfoDTO;
import org.sesac.slopedbe.roadreport.model.dto.ReportModalInfoDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportCallTaxiDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportCenterDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportFormDTO;
import org.sesac.slopedbe.roadreport.s3.exception.FileSizeLimitExceededException;
import org.sesac.slopedbe.roadreport.service.RoadReportCenterService;
import org.sesac.slopedbe.roadreport.service.RoadReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@RequestMapping("/api/roadReport")
@RestController
@Slf4j
public class RoadReportController {

    private final RoadReportService roadReportService;
    private final RoadReportCenterService roadReportCenterService;
    private final JwtUtil jwtUtil;

    private final GPTService gptService;

    @Operation(summary = "도로 제보 업로드", description = "통행 불편 제보를 업로드합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "통행 불편 제보가 성공적으로 제출되었습니다."),
        @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.")
    })
    @PostMapping("/upload")
    public ResponseEntity<String> addRoadReport(@RequestHeader("Authorization") String token, RoadReportFormDTO request) {
        try {
            String email = jwtUtil.extractEmailFromToken(token.substring(7));
            MemberOauthType oauthType = jwtUtil.extractOAuthTypeFromToken(token.substring(7));

            roadReportService.addRoadReport(email, oauthType, request);

            //gptService.sendImageWithMessage()

            return ResponseEntity.status(HttpStatus.CREATED).body("통행 불편 제보가 성공적으로 제출되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (FileSizeLimitExceededException e){
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류가 발생했습니다.");
        }
    }

    @ExceptionHandler(FileSizeLimitExceededException.class)
    public ResponseEntity<String> handleFileSizeLimitExceededException(FileSizeLimitExceededException e) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(e.getMessage());
    }

    @Operation(summary = "민원기관 연결", description = "통행불편 마커 위치에서 가장 가까운 민원기관을 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "가장 가까운 민원기관 정보 반환 성공"),
        @ApiResponse(responseCode = "404", description = "가까운 기관을 찾을 수 없음")
    })
    @PostMapping("/connect-center")
    public ResponseEntity<RoadReportCenterDTO> getClosestRoad(@RequestBody RoadMarkerInfoDTO request) {
        String cityName = AddressMapping.getMappedCity(request.getAddress());
        log.info("매핑된 도시명: {}", cityName);
        Optional<RoadReportCenterDTO> reportCenter = roadReportService.findClosestCenter(request.getLatitude(), request.getLongitude(), cityName);

        if (reportCenter.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(reportCenter.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "콜택시 연결", description = "통행불편 마커 위치에서 가장 가까운 콜택시를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "가장 가까운 콜택시 정보 반환 성공"),
        @ApiResponse(responseCode = "404", description = "가까운 콜택시를 찾을 수 없음")
    })
    @PostMapping("/connect-callTaxi")
    public ResponseEntity<RoadReportCallTaxiDTO> getClosestCallTaxi(@RequestBody RoadMarkerInfoDTO request) {
        String cityName = AddressMapping.getMappedCity(request.getAddress());
        Optional<RoadReportCallTaxiDTO> reportCallTaxi = roadReportService.findClosestCallTaxi(request.getLatitude(), request.getLongitude(), cityName);

        if (reportCallTaxi.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(reportCallTaxi.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "통행불편 제보 정보 조회", description = "주어진 ID를 가진 통행불편 제보의 상세 정보를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "통행불편 제보 정보 반환 성공"),
        @ApiResponse(responseCode = "404", description = "통행불편 제보를 찾을 수 없음")
    })
    @GetMapping("/info/{roadReportId}")
    public ResponseEntity<ReportModalInfoDTO> getReportInfo(@PathVariable(value = "roadReportId") Long roadReportId) {
        Optional<ReportModalInfoDTO> reportInfo = roadReportService.getReportInfo(roadReportId);
        return reportInfo.map(info -> new ResponseEntity<>(info, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "민원기관 리스트 조회", description = "모든 민원기관의 리스트를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "민원기관 리스트 반환 성공")
    })
    @GetMapping("/get-centerList")
    public ResponseEntity<List<RoadReportCenterDTO>> getCenterListInfo() {
        List<RoadReportCenterDTO> centerList = roadReportCenterService.getComplaintCenterList();
        return ResponseEntity.ok(centerList);
    }
}
