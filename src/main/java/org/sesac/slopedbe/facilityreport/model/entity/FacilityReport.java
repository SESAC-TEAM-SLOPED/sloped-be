package org.sesac.slopedbe.facilityreport.model.entity;

import org.sesac.slopedbe.common.entity.BaseTimeEntity;
import org.sesac.slopedbe.common.type.ReportStatus;
import org.sesac.slopedbe.facility.model.entity.Facility;
import org.sesac.slopedbe.member.model.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "facility_report")
@Entity
public class FacilityReport extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String content;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private String reasonForReject;

    @Column
    private Boolean isEntranceBarrier;

    @Column
    private Boolean hasSlope;

    @Column
    private Boolean hasElevator;

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @ManyToOne
    @JoinColumn(name = "email", nullable = false)
    private Member member;
}
