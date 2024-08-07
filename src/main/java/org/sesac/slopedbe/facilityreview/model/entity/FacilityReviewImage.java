package org.sesac.slopedbe.facilityreview.model.entity;

import org.sesac.slopedbe.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "facility_review_image")
@Entity
public class FacilityReviewImage extends BaseTimeEntity {
    @Id
    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private int uploadOrder;

    @ManyToOne
    @JoinColumn(name = "facility_review_id", nullable = false)
    private FacilityReview facilityReview;

    public FacilityReviewImage(String url, String fileName, int uploadOrder, FacilityReview facilityReview) {
        this.url = url;
        this.fileName = fileName;
        this.uploadOrder = uploadOrder;
        this.facilityReview = facilityReview;
    }

}
