package org.sesac.slopedbe.facilityreview.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sesac.slopedbe.common.entity.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "facility_review_image")
@Entity
public class FacilityReviewImage extends BaseTimeEntity {
    @Id
    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "facility_review_id", nullable = false)
    private FacilityReview facilityReview;
}
