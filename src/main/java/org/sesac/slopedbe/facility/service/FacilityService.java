package org.sesac.slopedbe.facility.service;

import java.util.List;

import org.sesac.slopedbe.bookmark.repository.BookmarkRepository;
import org.sesac.slopedbe.facility.exception.FacilityErrorCode;
import org.sesac.slopedbe.facility.exception.FacilityException;
import org.sesac.slopedbe.facility.model.dto.response.FacilityDetailResponse;
import org.sesac.slopedbe.facility.model.dto.response.FacilityResponse;
import org.sesac.slopedbe.facility.model.dto.response.FacilitySimpleResponse;
import org.sesac.slopedbe.facility.model.dto.vo.FacilitySimpleVO;
import org.sesac.slopedbe.facility.model.dto.vo.FacilityVO;
import org.sesac.slopedbe.facility.model.entity.Facility;
import org.sesac.slopedbe.facility.repository.FacilityRepository;
import org.sesac.slopedbe.facilityreview.repository.FacilityReviewRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FacilityReviewRepository facilityReviewRepository;

    public FacilityResponse getFacility(Long id) {
        FacilityVO facilityVO = facilityRepository.findFacilityById(id)
                .orElseThrow(() -> new FacilityException(FacilityErrorCode.FACILITY_NOT_FOUND));

        Boolean isBookmarked = null; // TODO: JWT 문제 해결 이후 북마크 여부 조회 추가
        return new FacilityResponse(facilityVO, null);
    }

    public FacilityDetailResponse getFacilityDetail(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new FacilityException(FacilityErrorCode.FACILITY_NOT_FOUND));
        return new FacilityDetailResponse(facility);
    }

    public List<FacilityResponse> getNearbyFacilities(double latitude, double longitude, double distanceInMeters, int limit, String type) {
        List<FacilityVO> facilities = facilityRepository.findNearbyFacilities(latitude, longitude, distanceInMeters, limit, type);
        return facilities.stream()
                .map(facility -> new FacilityResponse(facility, null))
                .toList();
    }

    public List<FacilitySimpleResponse> searchFacilities(String name, Double latitude, Double longitude) {
        List<FacilitySimpleVO> facilities = facilityRepository.findByNameWithDistance(name, latitude, longitude);
        return facilities.stream()
                .map(FacilitySimpleResponse::new)
                .toList();
    }
}
