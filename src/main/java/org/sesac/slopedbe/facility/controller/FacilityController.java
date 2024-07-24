package org.sesac.slopedbe.facility.controller;

import java.util.List;

import org.sesac.slopedbe.facility.model.dto.response.FacilityDetailResponse;
import org.sesac.slopedbe.facility.model.dto.response.FacilitySimpleResponse;
import org.sesac.slopedbe.facility.service.FacilityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/facilities")
@RestController
public class FacilityController {

    private final FacilityService facilityService;

    @GetMapping("/{id}")
    public ResponseEntity<FacilitySimpleResponse> getFacilityById(@PathVariable Long id) {
        return ResponseEntity.ok(facilityService.getFacility(id));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<FacilityDetailResponse> getFacilityDetailById(@PathVariable Long id) {
        return ResponseEntity.ok(facilityService.getFacilityDetail(id));
    }

    @GetMapping("")
    public ResponseEntity<List<FacilitySimpleResponse>> getNearbyFacilities(
        @RequestParam double latitude,
        @RequestParam double longitude,
        @RequestParam double distance,
        @RequestParam int limit
    ) {
        return ResponseEntity.ok(facilityService.getNearbyFacilities(latitude, longitude, distance, limit));
    }

}
