package org.sesac.slopedbe.roadreport.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoadReportCenterDTO {
	private Long id;
	private String centerName;
	private String centerContact;
}
