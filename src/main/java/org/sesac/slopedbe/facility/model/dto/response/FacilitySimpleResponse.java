package org.sesac.slopedbe.facility.model.dto.response;

import org.sesac.slopedbe.facility.model.dto.vo.FacilitySimpleVO;
import org.sesac.slopedbe.facility.model.type.FacilityType;

public record FacilitySimpleResponse(
	Long id,
	String name,
	String type,
	String address,
	Double distance_meters
){
	public FacilitySimpleResponse(FacilitySimpleVO facilitySimpleVO){
		this(
			facilitySimpleVO.getId(),
			facilitySimpleVO.getName(),
			FacilityType.getDisplayNameByValue(facilitySimpleVO.getType()),
			facilitySimpleVO.getAddress(),
			facilitySimpleVO.getDistanceMeters()
		);
	}
}
