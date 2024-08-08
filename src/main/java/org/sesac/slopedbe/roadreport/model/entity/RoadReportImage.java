package org.sesac.slopedbe.roadreport.model.entity;

import org.sesac.slopedbe.common.entity.BaseTimeEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "road_report_image")
@Entity
@Builder
@AllArgsConstructor
public class RoadReportImage extends BaseTimeEntity{

    @Id
    @Column(nullable = false)
    private String url;

    @Column(nullable = false, length = 200)
    private String fileName;

    @Column(nullable = false)
    private int uploadOrder;

    @ManyToOne
    @JoinColumn(name = "road_report_id", nullable = false)
    private RoadReport roadReport;

}
