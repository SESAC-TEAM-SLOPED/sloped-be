package org.sesac.slopedbe.facility.model.dto.response;

import org.sesac.slopedbe.facility.model.dto.vo.FacilityVO;

public record FacilitySimpleResponse (
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
	Boolean isBookmarked
){

	public FacilitySimpleResponse (FacilityVO facilityVO, Boolean isBookmarked){
		this(
			facilityVO.getId(),
			facilityVO.getName(),
			facilityVO.getAddress(),
			facilityVO.getType(),
			facilityVO.getLatitude().doubleValue(),
			facilityVO.getLongitude().doubleValue(),
			facilityVO.getCountOfReviews(),
			facilityVO.getCountOfConvenient(),
			facilityVO.getCountOfInconvenient(),
			facilityVO.getImageUrl(),
			isBookmarked
		);


	}
}
