package org.sesac.slopedbe.road.controller;

import org.sesac.slopedbe.road.service.RoadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/roads")
@RestController
public class RoadController {

    private final RoadService roadService;

    @PostMapping("/register")
    public ResponseEntity<String> createLocation() {
        roadService.createLocation();
        return ResponseEntity.ok("DB Saved");
    }


}
