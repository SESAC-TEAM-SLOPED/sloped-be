package org.sesac.slopedbe.facility.model.entity;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.annotations.Formula;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.sesac.slopedbe.bookmark.model.entity.Bookmark;
import org.sesac.slopedbe.common.entity.BaseTimeEntity;
import org.sesac.slopedbe.facility.model.type.FacilityType;
import org.sesac.slopedbe.facilityreview.model.entity.FacilityReview;
import org.sesac.slopedbe.member.model.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "facility")
@Entity
public class Facility extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(length = 20)
    private String contact;

    @Column(nullable = false)
    private Boolean isEntranceBarrier;

    @Column(nullable = false)
    private Boolean hasSlope;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point point;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FacilityType facilityType;

    private String businessHours;

    @Column(nullable = false)
    private Boolean hasElevator;

    @OneToMany(mappedBy = "facility")
    private List<FacilityReview> facilityReviews;

    @Formula("(SELECT COUNT(*) FROM facility_review fr WHERE fr.facility_id = id)")
    private Long reviewCount;

    @OneToMany(mappedBy = "facility")
    private List<Bookmark> bookmarks;

    @Formula("(SELECT COUNT(*) FROM facility_review fr WHERE fr.facility_id = id AND fr.is_convenient = false)")
    private Long countOfInconvenient;

    @Formula("(SELECT COUNT(*) FROM facility_review fr WHERE fr.facility_id = id AND fr.is_convenient = true)")
    private Long countOfConvenient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrar_id")
    private Member registrar;

    public BigDecimal getLatitude() {
        return point != null ? BigDecimal.valueOf(point.getY()) : null;
    }

    public BigDecimal getLongitude() {
        return point != null ? BigDecimal.valueOf(point.getX()) : null;
    }

    public void setLatitudeAndLongitude(BigDecimal latitude, BigDecimal longitude) {
        if (latitude != null && longitude != null) {
            GeometryFactory geometryFactory = new GeometryFactory();
            this.point = geometryFactory.createPoint(new Coordinate(longitude.doubleValue(), latitude.doubleValue()));
        }
    }
}
