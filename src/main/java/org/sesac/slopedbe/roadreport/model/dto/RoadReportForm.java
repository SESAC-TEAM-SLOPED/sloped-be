package org.sesac.slopedbe.roadreport.model.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoadReportForm {
	//private byte[] data;
	//private MultipartFile file;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private String content;
	private String address;


}
