package org.sesac.slopedbe.facility.model.entity;

import java.util.List;

import org.sesac.slopedbe.bookmark.model.entity.Bookmark;
import org.sesac.slopedbe.common.entity.BaseTimeEntity;
import org.sesac.slopedbe.facility.model.type.FacilityType;
import org.sesac.slopedbe.facilityreview.model.entity.FacilityReview;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

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

    @OneToMany(mappedBy = "facility")
    private List<Bookmark> bookmarks;
}
