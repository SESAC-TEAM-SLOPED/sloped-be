package org.sesac.slopedbe.roadreport.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.sesac.slopedbe.member.model.type.MemberOauthType;
import org.sesac.slopedbe.road.model.entity.Road;
import org.sesac.slopedbe.roadreport.model.dto.ReportModalInfoDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportCallTaxiDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportCenterDTO;
import org.sesac.slopedbe.roadreport.model.dto.RoadReportFormDTO;
import org.sesac.slopedbe.roadreport.model.entity.RoadReport;
import org.springframework.web.multipart.MultipartFile;

public interface RoadReportService {
	Road createAndSaveRoad(BigDecimal latitude, BigDecimal longitude, String address);	Optional<ReportModalInfoDTO> getReportInfo(Long roadId);
	RoadReport addRoadReport(String email, MemberOauthType oauthType,  RoadReportFormDTO request) throws IOException;
	List<String> saveRoadReportImages(List<MultipartFile> files, RoadReport newRoadReport) throws IOException;
	Optional<RoadReportCenterDTO> findClosestCenter(BigDecimal latitude, BigDecimal longitude, String cityName);
	Optional<RoadReportCallTaxiDTO> findClosestCallTaxi(BigDecimal latitude, BigDecimal longitude, String cityName);
}
