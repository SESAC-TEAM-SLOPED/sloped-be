package org.sesac.slopedbe.facility.model.dto.response;

import org.sesac.slopedbe.facility.model.entity.Facility;

public record FacilityDetailResponse (
	Long id,
	String name,
	String address,
	String type,
	Double latitude,
	Double longitude,
	Boolean hasSlope,
	Boolean isEntranceBarrier,
	Boolean hasElevator,
	String businessHours,
	String contact,
	String createdAt,
	String updatedAt
) {
	public FacilityDetailResponse(Facility facility) {
		this(
			facility.getId(),
			facility.getName(),
			facility.getAddress(),
			facility.getFacilityType().name(),
			facility.getPoint().getY(),
			facility.getPoint().getX(),
			facility.getHasSlope(),
			facility.getIsEntranceBarrier(),
			facility.getHasElevator(),
			facility.getBusinessHours(),
			facility.getContact(),
			facility.getCreatedAt().toString(),
			facility.getUpdatedAt().toString()
		);
	}
}
