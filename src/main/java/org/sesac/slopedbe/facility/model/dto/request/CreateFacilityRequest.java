package org.sesac.slopedbe.facility.model.dto.request;

import java.math.BigDecimal;

public record CreateFacilityRequest (
	String name,
	String address,
	String content,
	String contact,
	String facilityType,
	String businessHours,
	Boolean hasSlope,
	Boolean isEntranceBarrier,
	Boolean hasElevator,
	BigDecimal latitude,
	BigDecimal longitude
) {
}
