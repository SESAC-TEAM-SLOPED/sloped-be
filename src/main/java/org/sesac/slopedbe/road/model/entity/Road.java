package org.sesac.slopedbe.road.model.entity;

import java.math.BigDecimal;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.sesac.slopedbe.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "road")
@Entity
public class Road extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column()
    private Long id;

    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point point;

    private String content;

    @Column(name = "address", nullable = false)
    private String address;

    @Transient
    @Column(nullable = false)
    private BigDecimal latitude;

    @Transient
    @Column(nullable = false)
    private BigDecimal longitude;


    public static Road createRoad(BigDecimal latitude, BigDecimal longitude, String address) {
        GeometryFactory geometryFactory = new GeometryFactory();
        Point point = geometryFactory.createPoint(new Coordinate(longitude.doubleValue(), latitude.doubleValue()));
        return Road.builder()
            .point(point)
            //.content(content)
            .address(address)
            .build();
    }

}