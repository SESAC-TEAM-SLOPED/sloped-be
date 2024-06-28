package org.sesac.slopedbe.roadreport.model.entity;

import org.sesac.slopedbe.common.entity.BaseTimeEntity;
import org.sesac.slopedbe.common.type.ReportStatus;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.road.model.entity.Road;

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
@Table(name = "road_report")
@Entity
public class RoadReport extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private ReportStatus status;

    private String reasonForReject;

    @ManyToOne
    @JoinColumn(name = "road_id", nullable = false)
    private Road road;

    @ManyToOne
    @JoinColumn(name = "email", nullable = false)
    private Member member;
}
