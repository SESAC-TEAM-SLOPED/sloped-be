package org.sesac.slopedbe.facility.model.dto.vo;

import java.math.BigDecimal;

public interface FacilityVO {
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
