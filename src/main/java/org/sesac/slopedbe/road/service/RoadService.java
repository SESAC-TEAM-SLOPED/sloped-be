package org.sesac.slopedbe.road.service;

import java.util.List;

import org.sesac.slopedbe.road.model.dto.RoadMarkerInfoDTO;

public interface RoadService {
    List<RoadMarkerInfoDTO> getApprovedRoadPoints(double latitude, double longitude, double distance_meters, int limit);
    RoadMarkerInfoDTO findByRoaId(Long roadId);
}
