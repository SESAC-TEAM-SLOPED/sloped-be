package org.sesac.slopedbe.facilityreview.controller;

import lombok.RequiredArgsConstructor;
import org.sesac.slopedbe.facilityreview.service.FacilityReviewService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/facilities")
@RestController
public class FacilityReviewController {

    private final FacilityReviewService facilityReviewService;

}
