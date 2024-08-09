package org.sesac.slopedbe.facilityreview.service;

import java.util.concurrent.CompletableFuture;

import org.sesac.slopedbe.facilityreview.model.dto.response.ReviewAssessmentResponse;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewAssessmentService {
	private final RestTemplate restTemplate = new RestTemplate();

	@Async
	public CompletableFuture<ReviewAssessmentResponse> getAssessmentAsync(Long facilityId, Long reviewId) {
		String url = String.format("http://internal.togetheroad.me:8000/facility-reviews/assessment?facility_id=%d&review_id=%d",
			facilityId, reviewId);

		ReviewAssessmentResponse result = restTemplate.getForObject(url, ReviewAssessmentResponse.class);
		return CompletableFuture.completedFuture(result);
	}
}
