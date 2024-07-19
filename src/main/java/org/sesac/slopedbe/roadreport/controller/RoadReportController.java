package org.sesac.slopedbe.roadreport.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.sesac.slopedbe.road.model.dto.RoadMarkerInfoDTO;
import org.sesac.slopedbe.road.service.RoadKoreaCityService;
import org.sesac.slopedbe.roadreport.model.dto.ReportModalInfoDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportCallTaxiDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportCenterDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportFormDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportImageDTO;
import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportCallTaxi;
import org.sesac.slopedbe.roadreport.model.entity.RoadReportCenter;
import org.sesac.slopedbe.roadreport.s3.S3UploadImages;
import org.sesac.slopedbe.roadreport.service.RoadReportCallTaxiService;
import org.sesac.slopedbe.roadreport.service.RoadReportCenterService;
import org.sesac.slopedbe.roadreport.service.RoadReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    private final S3UploadImages s3UploadImages;
    private final RoadReportService roadReportService;
    private final RoadReportCenterService roadReportCenterService;
    private final RoadKoreaCityService cityService;
    private final RoadReportCallTaxiService roadReportCallTaxiService;
    private final String roadReportDir = "road_report";


    @Operation(summary = "도로 제보 업로드", description = "통행 불편 제보를 업로드합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "통행 불편 제보가 성공적으로 제출되었습니다."),
        @ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류가 발생했습니다.")
    })
    @PostMapping("upload")
    public ResponseEntity<String> addRoadReport(RoadReportFormDTO request) throws IOException {
        log.info("통행 불편 제보 업로드 시작");
        try {
            RoadReport newRoadReport = roadReportService.addRoadReport(request);

            for (int i = 0; i < request.getFiles().size(); i++) {
                String uuid = UUID.randomUUID().toString();
                String saveFileName = uuid + "_" + request.getFiles().get(i).getOriginalFilename();
                String fileUrl = s3UploadImages.upload(request.getFiles().get(i), roadReportDir, saveFileName);

                if (!request.getFiles().get(i).getContentType().startsWith("image")) {
                    throw new IllegalArgumentException("이미지가 아닌 파일은 업로드할 수 없습니다.");
                }

                RoadReportImageDTO roadReportImageDTO = RoadReportImageDTO.builder()
                    .url(fileUrl)
                    .fileName(request.getFiles().get(i).getOriginalFilename())
                    .roadReportId(newRoadReport.getId())
                    .uploadOrder(i)
                    .build();

                roadReportService.createRoadReportImage(roadReportImageDTO);
                log.info("통행불편 제보 이미지 업로드 완료: {}", saveFileName);
            }
            log.info("통행 불편 제보가 성공적으로 제출되었습니다.");
            return ResponseEntity.status(HttpStatus.CREATED).body("통행 불편 제보가 성공적으로 제출되었습니다.");

        } catch (IllegalArgumentException e) {
            log.error("[통행불편 제보] 잘못된 요청: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            log.error("[통행불편 제보] 파일 업로드 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 중 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("[통행제보] 서버 내부 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 내부 오류가 발생했습니다.");
        }
    }

    @Operation(summary = "민원기관 연결", description = "통행불편 마커 위치에서 가장 가까운 민원기관을 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "가장 가까운 민원기관 정보 반환 성공"),
        @ApiResponse(responseCode = "404", description = "가까운 기관을 찾을 수 없음")
    })
    @PostMapping("/connect-center")
    public ResponseEntity<RoadReportCenterDTO> getClosestRoad(@RequestBody RoadMarkerInfoDTO request) {
        String cityName = request.getAddress().split(" ")[0];
        log.info("민원기관 연결 요청 - 도시명: {}", cityName);
        Double latitude = request.getLatitude().doubleValue();
        Double longitude = request.getLongitude().doubleValue();

        String mappingCity = cityService.findMappingCity(cityName);
        Optional<RoadReportCenter> reportCenter = roadReportCenterService.findClosestCenter(latitude, longitude, mappingCity);

        if (reportCenter.isPresent()) {
            RoadReportCenterDTO reportCenterDTO = RoadReportCenterDTO.builder()
                .id(reportCenter.get().getId())
                .centerName(reportCenter.get().getCenterName())
                .centerContact(reportCenter.get().getCenterContact())
                .build();
            log.info("가장 가까운 민원기관 찾음 - 기관명: {}", reportCenter.get().getCenterName());
            return ResponseEntity.status(HttpStatus.OK).body(reportCenterDTO);
        } else {
            log.warn("가까운 민원 기관을 찾을 수 없음");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }

    @Operation(summary = "콜택시 연결", description = "통행불편 마커 위치에서 가장 가까운 콜택시를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "가장 가까운 콜택시 정보 반환 성공"),
        @ApiResponse(responseCode = "404", description = "가까운 콜택시를 찾을 수 없음")
    })
    @GetMapping("/connect-callTaxi")
    public ResponseEntity<RoadReportCallTaxiDTO> getClosestCallTaxi(RoadMarkerInfoDTO request) {
        String cityName = null;

        switch (request.getAddress()) {
            case "경남":
                cityName = "경상남도";
                break;
            case "경북":
                cityName = "경상북도";
                break;
            case "전남":
                cityName = "전라남도";
                break;
            default:
                cityName = request.getAddress().split(" ")[0];
        }
        log.info("콜택시 연결 요청 - 도시명: {}", cityName);

        Optional<RoadReportCallTaxi> reportCallTaxi = roadReportCallTaxiService.findClosestCallTaxi(request.getLatitude(), request.getLongitude(), cityName);

        if (reportCallTaxi.isPresent()) {
            RoadReportCallTaxi callTaxi = reportCallTaxi.get();
            RoadReportCallTaxiDTO callTaxiDTO = RoadReportCallTaxiDTO.builder()
                .callTaxiName(callTaxi.getCallTaxiName())
                .callTaxiContact(callTaxi.getCallTaxiContact())
                .homePage(callTaxi.getHomePage())
                .canOnlineReserve(callTaxi.isCanOnlineReserve())
                .build();
            log.info("가장 가까운 콜택시 찾음 - 콜택시명: {}", callTaxi.getCallTaxiName());
            return ResponseEntity.status(HttpStatus.OK).body(callTaxiDTO);
        } else {
            log.warn("가까운 콜택시를 찾을 수 없음");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


	@GetMapping("/info/{roadReportId}")
	public ResponseEntity<ReportModalInfoDTO> getReportInfo(@PathVariable Long roadReportId) {
		ReportModalInfoDTO reportInfo = roadReportService.getReportInfo(roadReportId);
		return new ResponseEntity<>(reportInfo, HttpStatus.OK);
	}

    @GetMapping("/get-centerList")
    public ResponseEntity<List<RoadReportCenterDTO>> getCenterListInfo() {
        List<RoadReportCenterDTO> centerList = roadReportCenterService.getComplaintCenterList();
        return ResponseEntity.ok(centerList);
    }


}
