package org.sesac.slopedbe.facility.service;

import org.sesac.slopedbe.bookmark.repository.BookmarkRepository;
import org.sesac.slopedbe.facility.exception.FacilityErrorCode;
import org.sesac.slopedbe.facility.exception.FacilityException;
import org.sesac.slopedbe.facility.model.dto.response.FacilitySimpleResponse;
import org.sesac.slopedbe.facility.model.dto.vo.FacilityVO;
import org.sesac.slopedbe.facility.repository.FacilityRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FacilityService {

    private final FacilityRepository facilityRepository;
    private final BookmarkRepository bookmarkRepository;


    public FacilitySimpleResponse getFacility(Long id) {
        FacilityVO facilityVO = facilityRepository.findFacilityDtoById(id)
                .orElseThrow(() -> new FacilityException(FacilityErrorCode.FACILITY_NOT_FOUND));

        Boolean isBookmarked = null; // TODO: JWT 문제 해결 이후 북마크 여부 조회 추가
        return new FacilitySimpleResponse(facilityVO, null);
    }

}
