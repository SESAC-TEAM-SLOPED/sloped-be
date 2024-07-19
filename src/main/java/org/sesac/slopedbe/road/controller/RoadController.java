package org.sesac.slopedbe.road.controller;

import java.util.List;

import org.sesac.slopedbe.road.model.dto.RoadMarkerInfoDTO;
import org.sesac.slopedbe.road.service.RoadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/roads")
@RestController
public class RoadController {

    private final RoadService roadService;
    @GetMapping("/get-points")
    public ResponseEntity<List<RoadMarkerInfoDTO>> getApprovedRoadPoints() {
        List<RoadMarkerInfoDTO> approvedPoints = roadService.getApprovedRoadPoints();
        return ResponseEntity.ok(approvedPoints);
    }

    @GetMapping("/{roadId}")
    public RoadMarkerInfoDTO getRoadInfo(@PathVariable Long roadId) {
        RoadMarkerInfoDTO roadDTO = roadService.findByRoaId(roadId);
        return roadDTO;
    }
}
