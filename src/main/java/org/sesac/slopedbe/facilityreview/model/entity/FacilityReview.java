package org.sesac.slopedbe.facilityreview.model.entity;

import org.sesac.slopedbe.common.entity.BaseTimeEntity;
import org.sesac.slopedbe.facility.model.entity.Facility;
import org.sesac.slopedbe.member.model.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "facility_review")
@Entity
public class FacilityReview extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private Boolean isConvenient;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "email", referencedColumnName = "email"),
        @JoinColumn(name = "oauthType", referencedColumnName = "oauthType")
    })
    private Member member;

    private Long accessibilityScore;
    private String accessibilityDescription;
}
