package org.sesac.slopedbe.road.service;

import java.util.List;

import org.sesac.slopedbe.road.model.dto.RoadMarkerInfoDTO;

public interface RoadService {
    List<RoadMarkerInfoDTO> getApprovedRoadPoints();
    RoadMarkerInfoDTO findByRoaId(Long roadId);
}
