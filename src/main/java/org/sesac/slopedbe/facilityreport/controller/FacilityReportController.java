package org.sesac.slopedbe.facilityreport.controller;

import lombok.RequiredArgsConstructor;
import org.sesac.slopedbe.facilityreport.service.FacilityReportService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/facilities")
@RestController
public class FacilityReportController {

    private final FacilityReportService facilityReportService;

}
