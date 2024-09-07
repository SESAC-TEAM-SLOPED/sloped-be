package org.sesac.slopedbe.facilityreview.service;

import lombok.RequiredArgsConstructor;
import org.sesac.slopedbe.facilityreview.repository.FacilityReviewRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FacilityReviewService {

    private final FacilityReviewRepository facilityReviewRepository;

}
