package org.sesac.slopedbe.road.controller;

import org.sesac.slopedbe.road.service.RoadService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/roads")
@RestController
public class RoadController {

    private final RoadService roadService;
}
