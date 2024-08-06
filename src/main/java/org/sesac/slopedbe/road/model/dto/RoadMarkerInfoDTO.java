package org.sesac.slopedbe.road.model.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoadMarkerInfoDTO {
	private Long id;
	private BigDecimal latitude;
	private BigDecimal longitude;
	private String address;
}
