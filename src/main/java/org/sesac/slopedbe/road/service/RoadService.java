package org.sesac.slopedbe.road.service;

import lombok.RequiredArgsConstructor;
import org.sesac.slopedbe.road.repository.RoadRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RoadService {

    private final RoadRepository roadRepository;

}
