package org.sesac.slopedbe.roadreport.repository;

import java.util.List;
import java.util.Optional;

import org.sesac.slopedbe.roadreport.model.entity.RoadReportImage;
import org.springframework.data.repository.CrudRepository;

public interface RoadReportImageRepository extends CrudRepository<RoadReportImage, String>{
	Optional<List<RoadReportImage>> findByRoadReportId(Long roadReportId);
}
