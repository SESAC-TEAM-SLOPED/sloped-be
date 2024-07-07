package org.sesac.slopedbe.road.service;

import java.math.BigDecimal;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.sesac.slopedbe.road.model.entity.Road;
import org.sesac.slopedbe.road.repository.RoadRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoadService {

    private final RoadRepository roadRepository;

    public Road createLocation() {
        GeometryFactory geometryFactory = new GeometryFactory();
        BigDecimal latitude = new BigDecimal("37.7749");
        BigDecimal longitude = new BigDecimal("-122.4194");
        String content = "This is a road.";
        String address = "123 Road St.";
        Point point = geometryFactory.createPoint(new Coordinate(longitude.doubleValue(), latitude.doubleValue()));

        Road road = Road.createRoad(latitude, longitude, content, address);
        return roadRepository.save(road);
    }

}
