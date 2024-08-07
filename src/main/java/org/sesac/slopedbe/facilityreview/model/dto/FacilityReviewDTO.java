package org.sesac.slopedbe.facilityreview.model.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;

@Getter
public class FacilityReviewDTO {
	private Long facilityId;
	private Long facilityReviewId;
	private Boolean isConvenient;
	private String context;
	private List<MultipartFile> files;
}
