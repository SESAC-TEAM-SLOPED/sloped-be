package org.sesac.slopedbe.roadreport.controller;

import lombok.RequiredArgsConstructor;
import org.sesac.slopedbe.roadreport.service.RoadReportService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/roads")
@RestController
public class RoadReportController {

    private final RoadReportService roadReportService;

}
