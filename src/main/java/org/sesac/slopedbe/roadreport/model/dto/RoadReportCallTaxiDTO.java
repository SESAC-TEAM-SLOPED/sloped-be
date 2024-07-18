package org.sesac.slopedbe.roadreport.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoadReportCallTaxiDTO {
	private String callTaxiContact;
	private String callTaxiName;
	private String homePage;
	private boolean canOnlineReserve;
}
