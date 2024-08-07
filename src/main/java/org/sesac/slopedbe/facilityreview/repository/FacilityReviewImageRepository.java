package org.sesac.slopedbe.facilityreview.repository;

import java.util.List;

import org.sesac.slopedbe.facilityreview.model.entity.FacilityReviewImage;
import org.springframework.data.repository.CrudRepository;

public interface FacilityReviewImageRepository extends CrudRepository<FacilityReviewImage, String> {
	List<FacilityReviewImage> findByFacilityReviewId(Long facilityReviewId);
}
