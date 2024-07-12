package org.sesac.slopedbe.roadreport.controller;

import java.io.IOException;

import org.sesac.slopedbe.roadreport.model.dto.RoadReportForm;
import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.sesac.slopedbe.roadreport.s3.S3UploadImages;
import org.sesac.slopedbe.roadreport.service.RoadReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("register")
    public ResponseEntity<String> addRoadReport(RoadReportForm request) throws IOException {
        /*for (int i = 0; i < request.getFiles().size(); i++) {
            System.out.println(request.getFiles().get(i).getResource().getFilename());
        }*/

       // s3UploadImages.upload(request.getFiles().get(0), request.getFiles().get(0).getName());
        s3UploadImages.putS3(request.getFiles().get(0).getBytes(),"test");



        RoadReport newRoadReport = roadReportService.addRoadReport(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("통행 불편 제보가 성공적으로 제출되었습니다.");
    }


}
