package org.sesac.slopedbe.facility.controller;

import java.util.List;

import org.sesac.slopedbe.facility.exception.FacilityErrorCode;
import org.sesac.slopedbe.facility.exception.FacilityException;
import org.sesac.slopedbe.facility.model.dto.response.FacilityDetailResponse;
import org.sesac.slopedbe.facility.model.dto.response.FacilityResponse;
import org.sesac.slopedbe.facility.model.dto.response.FacilitySimpleResponse;
import org.sesac.slopedbe.facility.service.FacilityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<FacilityResponse> getFacilityById(@PathVariable Long id) {
        return ResponseEntity.ok(facilityService.getFacility(id));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<FacilityDetailResponse> getFacilityDetailById(@PathVariable Long id) {
        return ResponseEntity.ok(facilityService.getFacilityDetail(id));
    }

    @GetMapping("")
    public ResponseEntity<List<FacilityResponse>> getNearbyFacilities(
        @RequestParam double latitude,
        @RequestParam double longitude,
        @RequestParam double distance_meters,
        @RequestParam int limit,
        @RequestParam(required = false) String type
    ) {
        if (limit > 100) {
            throw new FacilityException(FacilityErrorCode.FIND_FACILITY_LIMIT_EXCEEDED);
        }

        return ResponseEntity.ok(facilityService.getNearbyFacilities(latitude, longitude, distance_meters, limit,
            type));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FacilitySimpleResponse>> searchFacilities(
        @RequestParam String name,
        @RequestParam Double latitude,
        @RequestParam Double longitude
    ) {
        return ResponseEntity.ok(facilityService.searchFacilities(name, latitude, longitude));
    }

    @GetMapping("/all")
    public ResponseEntity<Page<FacilityDetailResponse>> getAllFacilities(Pageable pageable) {
        return ResponseEntity.ok(facilityService.getAllFacilities(pageable));
    }
}
