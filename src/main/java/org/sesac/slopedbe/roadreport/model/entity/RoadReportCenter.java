package org.sesac.slopedbe.roadreport.model.entity;

import org.sesac.slopedbe.road.model.entity.Road;
import org.sesac.slopedbe.road.model.entity.RoadKoreaCity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name ="road_report_center")
public class RoadReportCenter {
	//지역,기관명,전화번호,주소,위도,경도
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable=false)
	private String centerName;

	@Column(nullable=false)
	private String centerContact;

	@ManyToOne
	@JoinColumn(name = "road_id", nullable = false)
	private Road road;

	@ManyToOne
	@JoinColumn(name = "city_id", nullable = false)
	private RoadKoreaCity city;

}
