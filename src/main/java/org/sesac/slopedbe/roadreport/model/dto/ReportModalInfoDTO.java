package org.sesac.slopedbe.roadreport.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ReportModalInfoDTO {
	private Long id;
	private List<RoadReportImageDTO> reportImageList;
	private String content;

}
