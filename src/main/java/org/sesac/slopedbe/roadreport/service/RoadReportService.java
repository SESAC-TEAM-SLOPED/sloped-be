package org.sesac.slopedbe.roadreport.service;

import lombok.RequiredArgsConstructor;
import org.sesac.slopedbe.roadreport.repository.RoadReportRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class  RoadReportService {

    private final RoadReportRepository roadReportRepository;

}
