package org.sesac.slopedbe.facility.service;

import org.sesac.slopedbe.facility.exception.FacilityErrorCode;
import org.sesac.slopedbe.facility.exception.FacilityException;
import org.sesac.slopedbe.facility.model.dto.response.FacilityDto;
import org.sesac.slopedbe.facility.repository.FacilityRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FacilityService {

    private final FacilityRepository facilityRepository;

    public FacilityDto getFacilityById(Long id) {
        FacilityDto facilityDto = facilityRepository.findFacilityDtoById(id)
                .orElseThrow(() -> new FacilityException(FacilityErrorCode.FACILITY_NOT_FOUND));
        // return new FacilityDto(
        //     facility.getId(),
        //     facility.getName(),
        //     facility.getAddress(),
        //     facility.getFacilityType().getName(),
        //     facility.getLatitude(),
        //     facility.getLongitude(),
        //     facility.getFacilityReviews().size(),
        //
        // )

        return facilityDto;
    }

}
