package org.sesac.slopedbe.road.controller;

import java.util.List;

import org.sesac.slopedbe.road.model.dto.RoadMarkerInfoDTO;
import org.sesac.slopedbe.road.service.RoadService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/roads")
@RestController
public class RoadController {

    private final RoadService roadService;
    @GetMapping("/getPoints")
    public List<RoadMarkerInfoDTO> getApprovedRoadPoints() {
        return roadService.getApprovedRoadPoints();
    }
}
