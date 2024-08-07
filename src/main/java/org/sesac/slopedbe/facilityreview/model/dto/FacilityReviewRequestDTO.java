package org.sesac.slopedbe.facilityreview.model.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;

@Getter
public class FacilityReviewRequestDTO {
	private Long facilityId;
	private Long facilityReviewId;
	private Boolean isConvenient;
	private String content;
	private List<MultipartFile> files;
}
