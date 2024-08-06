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
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final BookmarkRepository bookmarkRepository;
    private final FacilityReviewRepository facilityReviewRepository;

    public FacilityResponse getFacility(Long id, MemberCompositeKey memberCK) {
        FacilityVO facilityVO = facilityRepository.findFacilityById(id)
                .orElseThrow(() -> new FacilityException(FacilityErrorCode.FACILITY_NOT_FOUND));

        Boolean isBookmarked = null;
        if(memberCK == null) {
            isBookmarked = null;
        } else {
            isBookmarked = bookmarkRepository.existsByMember_Id_EmailAndMember_Id_OauthTypeAndFacility_Id(memberCK.getEmail(), memberCK.getOauthType(), id);
        }
        return new FacilityResponse(facilityVO, isBookmarked);
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

    public Page<FacilityDetailResponse> getAllFacilities(Pageable pageable) {
        Page<Facility> facilities = facilityRepository.findAll(pageable);
        return facilities.map(FacilityDetailResponse::new);
    }

    public void deleteFacility(Long id) {
        Facility facility = facilityRepository.findById(id)
                .orElseThrow(() -> new FacilityException(FacilityErrorCode.FACILITY_NOT_FOUND));
        facilityRepository.delete(facility);
    }

    public Long saveFacility(Facility facility) {
        return facilityRepository.saveFacilityAndReturnId(
            facility.getName(),
            facility.getAddress(),
            facility.getContent(),
            facility.getContact(),
            facility.getFacilityType().name(),
            facility.getBusinessHours(),
            facility.getHasSlope(),
            facility.getIsEntranceBarrier(),
            facility.getHasElevator(),
            facility.getLongitude(),
            facility.getLatitude()
        );
    }
}
