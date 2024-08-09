package org.sesac.slopedbe.facility.model.vo;

public interface FacilityScoreVO {
	Long getId();
	String getName();
	String getAddress();
	String getType();
	Double getLatitude();
	Double getLongitude();
	Long getCountOfReviews();
	Long getCountOfConvenient();
	Long getCountOfInconvenient();
	String getImageUrl();
	Long getAverageAccessibilityScore();
	String getAccessibilityDescription();
}
