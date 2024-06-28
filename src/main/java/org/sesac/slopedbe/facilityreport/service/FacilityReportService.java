package org.sesac.slopedbe.facilityreport.service;

import lombok.RequiredArgsConstructor;
import org.sesac.slopedbe.facilityreport.repository.FacilityReportRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class FacilityReportService {

    private final FacilityReportRepository facilityReportRepository;

}
