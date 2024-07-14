package org.sesac.slopedbe.roadreport.model.dto;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoadReportFormDTO {
	private BigDecimal latitude;
	private BigDecimal longitude;
	private String content;
	private String address;
	private List<MultipartFile> files;
}
