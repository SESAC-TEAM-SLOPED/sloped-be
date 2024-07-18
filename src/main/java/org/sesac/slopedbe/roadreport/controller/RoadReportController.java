package org.sesac.slopedbe.roadreport.controller;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.sesac.slopedbe.road.model.dto.RoadMarkerInfoDTO;
import org.sesac.slopedbe.road.service.RoadKoreaCityService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/roadReport")
@RestController
public class RoadReportController {
    private final S3UploadImages s3UploadImages;
    private final RoadReportService roadReportService;
    private final RoadReportCenterService roadReportCenterService;
    private final RoadKoreaCityService cityService;
    private final RoadReportCallTaxiService roadReportCallTaxiService;
    private final String roadReportDir = "road_report";

    @PostMapping("upload")
    public ResponseEntity<String> addRoadReport(RoadReportFormDTO request) throws IOException {

        RoadReport newRoadReport = roadReportService.addRoadReport(request);

        for (int i = 0; i < request.getFiles().size(); i++) {
            String uuid = UUID.randomUUID().toString();
            String saveFileName = uuid + "_" + request.getFiles().get(i).getOriginalFilename();
            String fileUrl = s3UploadImages.upload(request.getFiles().get(i), roadReportDir, saveFileName);

            if(!request.getFiles().get(i).getContentType().startsWith("image")){
                throw new IllegalArgumentException("이미지가 아닌 파일은 업로드할 수 없습니다.");
            }

            RoadReportImageDTO roadReportImageDTO = RoadReportImageDTO.builder()
                .url(fileUrl)
                .fileName(saveFileName)
                .roadReportId(newRoadReport.getId())
                .uploadOrder(i)
                .build();

            roadReportService.createRoadReportImage(roadReportImageDTO);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body("통행 불편 제보가 성공적으로 제출되었습니다.");
    }


    @GetMapping("/connect-center")
    public ResponseEntity<RoadReportCenterDTO> getClosestRoad(RoadMarkerInfoDTO request) {
        String cityName = request.getAddress().split(" ")[0];

        String mappingCity = cityService.findMappingCity(cityName);
        Optional<RoadReportCenter> reportCenter = roadReportCenterService.findClosestCenter(request.getLatitude(), request.getLongitude(), mappingCity);

        if (reportCenter.isPresent()) {
            RoadReportCenterDTO reportCenterDTO = RoadReportCenterDTO.builder()
                .id(reportCenter.get().getId())
                .centerName(reportCenter.get().getCenterName())
                .centerContact(reportCenter.get().getCenterContact())
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(reportCenterDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

    }

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

        Optional<RoadReportCallTaxi> reportCallTaxi = roadReportCallTaxiService.findClosestCallTaxi(request.getLatitude(), request.getLongitude(), cityName);

        if (reportCallTaxi.isPresent()) {
            RoadReportCallTaxi callTaxi = reportCallTaxi.get();
            RoadReportCallTaxiDTO callTaxiDTO = RoadReportCallTaxiDTO.builder()
                .callTaxiName(callTaxi.getCallTaxiName())
                .callTaxiContact(callTaxi.getCallTaxiContact())
                .homePage(callTaxi.getHomePage())
                .canOnlineReserve(callTaxi.isCanOnlineReserve())
                .build();
            return ResponseEntity.status(HttpStatus.OK).body(callTaxiDTO);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
