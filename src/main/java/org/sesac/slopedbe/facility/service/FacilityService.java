package org.sesac.slopedbe.facility.service;

import lombok.RequiredArgsConstructor;
import org.sesac.slopedbe.facility.repository.FacilityRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FacilityService {

    private final FacilityRepository facilityRepository;

}
