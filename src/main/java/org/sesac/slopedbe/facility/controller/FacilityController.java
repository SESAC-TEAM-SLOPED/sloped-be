package org.sesac.slopedbe.facility.controller;

import lombok.RequiredArgsConstructor;
import org.sesac.slopedbe.facility.service.FacilityService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/facilities")
@RestController
public class FacilityController {

    private final FacilityService facilityService;

}
