package org.sesac.slopedbe.roadreport.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReportModalInfoDTO {
	private Long id;
	private List<RoadReportImageDTO> reportImageList;
	private String content;
	private String imageCaption;
}
