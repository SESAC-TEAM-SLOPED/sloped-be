package org.sesac.slopedbe.facilityreview.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.sesac.slopedbe.facilityreview.model.entity.FacilityReview;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FacilityReviewResponseDTO {
	private Long facilityReviewId;
	private String facilityName;
	private String nickname;
	private boolean isDisability;
	private Boolean isConvenient;
	private String content;
	private List<String> urls;
	private LocalDateTime updatedAt;

	public static FacilityReviewResponseDTO toReviewResponseDTO(FacilityReview facilityReview, List<String> urls) {
		return new FacilityReviewResponseDTO(
			facilityReview.getId(),
			facilityReview.getFacility().getName(),
			facilityReview.getMember().getNickname(),
			facilityReview.getMember().isDisability(),
			facilityReview.getIsConvenient(),
			facilityReview.getContent(),
			urls,
			facilityReview.getUpdatedAt()
		);
	}
}
