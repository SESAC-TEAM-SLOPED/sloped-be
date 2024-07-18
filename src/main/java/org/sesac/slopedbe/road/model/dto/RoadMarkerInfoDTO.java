package org.sesac.slopedbe.road.model.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoadMarkerInfoDTO {
	private BigDecimal latitude;
	private BigDecimal longitude;
	private String address;
}
