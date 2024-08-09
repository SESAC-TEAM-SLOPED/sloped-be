package org.sesac.slopedbe.gpt.service;

import java.io.IOException;
import java.util.List;

import org.sesac.slopedbe.facilityreview.model.entity.FacilityReviewImage;

public interface GPTService {

	void generateReviewImageCaption(List<FacilityReviewImage> images) throws IOException;
	void generateRoadImageCaption(Long roadReportId,
		List<String> images) throws IOException;
}
