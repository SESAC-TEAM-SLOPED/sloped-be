package org.sesac.slopedbe.facilityreview.model.dto;

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
public class FacilityReviewRequestDTO {
	private Boolean isConvenient;
	private String content;
	private List<MultipartFile> files;
}
