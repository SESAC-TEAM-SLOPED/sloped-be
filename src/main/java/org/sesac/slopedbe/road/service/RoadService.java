package org.sesac.slopedbe.road.service;

import org.sesac.slopedbe.road.repository.RoadRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoadService {

    private final RoadRepository roadRepository;

}
