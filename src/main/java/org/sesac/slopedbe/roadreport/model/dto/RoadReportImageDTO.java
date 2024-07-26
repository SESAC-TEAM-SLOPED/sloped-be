package org.sesac.slopedbe.roadreport.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoadReportImageDTO {
	private String url;
	private int uploadOrder;
	private String fileName;
	private Long roadReportId;

}
