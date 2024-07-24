package org.sesac.slopedbe.facility.service;

import java.util.List;

import org.sesac.slopedbe.bookmark.repository.BookmarkRepository;
import org.sesac.slopedbe.facility.exception.FacilityErrorCode;
import org.sesac.slopedbe.facility.exception.FacilityException;
import org.sesac.slopedbe.facility.model.dto.response.FacilityDetailResponse;
import org.sesac.slopedbe.facility.model.dto.response.FacilitySimpleResponse;
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

    public FacilitySimpleResponse getFacility(Long id) {
        FacilityVO facilityVO = facilityRepository.findFacilityDtoById(id)
                .orElseThrow(() -> new FacilityException(FacilityErrorCode.FACILITY_NOT_FOUND));

        Boolean isBookmarked = null; // TODO: JWT 문제 해결 이후 북마크 여부 조회 추가
        return new FacilitySimpleResponse(facilityVO, null);
    }

    public FacilityDetailResponse getFacilityDetail(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new FacilityException(FacilityErrorCode.FACILITY_NOT_FOUND));
        return new FacilityDetailResponse(facility);
    }

    public List<FacilitySimpleResponse> getNearbyFacilities(double latitude, double longitude, double distanceInMeters, int limit) {
        List<FacilityVO> facilities = facilityRepository.findNearbyFacilitiesDetailed(latitude, longitude, distanceInMeters, limit);
        return facilities.stream()
                .map(facility -> new FacilitySimpleResponse(facility, null))
                .toList();
    }

}
