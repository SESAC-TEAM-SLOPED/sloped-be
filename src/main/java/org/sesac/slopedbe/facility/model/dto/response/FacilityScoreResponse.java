package org.sesac.slopedbe.facility.model.dto.response;

import org.sesac.slopedbe.facility.model.vo.FacilityScoreVO;

public record FacilityScoreResponse (
	Long id,
	String name,
	String address,
	String type,
	Double latitude,
	Double longitude,
	Long countOfReviews,
	Long countOfConvenient,
	Long countOfInconvenient,
	String imageUrl,
	Long averageAccessibilityScore,
	String accessibilityDescription
){
	public FacilityScoreResponse(FacilityScoreVO facilityScoreVO){
		this(
			facilityScoreVO.getId(),
			facilityScoreVO.getName(),
			facilityScoreVO.getAddress(),
			facilityScoreVO.getType(),
			facilityScoreVO.getLatitude(),
			facilityScoreVO.getLongitude(),
			facilityScoreVO.getCountOfReviews(),
			facilityScoreVO.getCountOfConvenient(),
			facilityScoreVO.getCountOfInconvenient(),
			facilityScoreVO.getImageUrl(),
			facilityScoreVO.getAverageAccessibilityScore(),
			facilityScoreVO.getAccessibilityDescription()
		);
	}
}
