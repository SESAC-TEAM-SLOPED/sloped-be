package org.sesac.slopedbe.road.controller;

import lombok.RequiredArgsConstructor;
import org.sesac.slopedbe.road.service.RoadService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/roads")
@RestController
public class RoadController {

    private final RoadService roadService;

}
