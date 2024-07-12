package org.sesac.slopedbe.roadreport.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoadReportImageDTO {
	private String url;
	private int uploadOrder;
	private String fileName;
	private Long roadReportId;

}
