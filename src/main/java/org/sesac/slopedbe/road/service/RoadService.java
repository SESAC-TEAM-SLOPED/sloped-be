package org.sesac.slopedbe.road.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.sesac.slopedbe.road.model.dto.RoadMarkerInfoDTO;
import org.sesac.slopedbe.road.model.entity.Road;

public interface RoadService {
    List<RoadMarkerInfoDTO> getApprovedRoadPoints();
    Optional<Road> findClosestRoad(BigDecimal latitude, BigDecimal longitude);
}
