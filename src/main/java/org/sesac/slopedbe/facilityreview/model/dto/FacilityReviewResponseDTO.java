package org.sesac.slopedbe.facilityreview.model.dto;

import org.sesac.slopedbe.facilityreview.model.entity.FacilityReview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacilityReviewResponseDTO {
	private Long facilityId;
	private String name;
	private Boolean isConvenient;
	private String context;

	public static FacilityReviewResponseDTO toReviewResponseDTO(FacilityReview facilityReview) {
		return new FacilityReviewResponseDTO(
			facilityReview.getFacility().getId(),
			facilityReview.getFacility().getName(),
			facilityReview.getIsConvenient(),
			facilityReview.getContent()
		);
	}
}
