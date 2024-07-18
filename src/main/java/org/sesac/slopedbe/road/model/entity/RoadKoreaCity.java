package org.sesac.slopedbe.road.model.entity;

import java.util.List;

import org.sesac.slopedbe.roadreport.model.entity.RoadReportCenter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name ="road_korea_city")
public class RoadKoreaCity {
	//시도, 지역명, 민원기관지역
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false)
	private Long id;

	@Column(nullable=false)
	private String cityName;

	@Column(nullable = false)
	private String regionName;

	@Column(nullable = false)
	private String complaintRegion;

	@OneToMany(mappedBy = "city")
	private List<RoadReportCenter> roadReportCenters;

}
