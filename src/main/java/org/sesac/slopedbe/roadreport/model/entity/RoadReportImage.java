package org.sesac.slopedbe.roadreport.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sesac.slopedbe.common.entity.BaseTimeEntity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "road_report_image")
@Entity
public class RoadReportImage extends BaseTimeEntity {

    @Id
    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "road_report_id", nullable = false)
    private RoadReport roadReport;
}
