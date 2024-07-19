package org.sesac.slopedbe.facility.model.dto.response;

import java.math.BigDecimal;

public interface FacilityDto {
	Long getId();
	String getName();
	String getAddress();
	String getType();
	BigDecimal getLatitude();
	BigDecimal getLongitude();
	Long getCountOfReviews();
	Long getCountOfConvenient();
	Long getCountOfInconvenient();
	String getImageUrl();
}
