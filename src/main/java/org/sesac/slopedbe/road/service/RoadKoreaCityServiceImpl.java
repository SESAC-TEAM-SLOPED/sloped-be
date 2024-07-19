package org.sesac.slopedbe.road.service;

import org.sesac.slopedbe.road.repository.RoadKoreaCityRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoadKoreaCityServiceImpl  implements RoadKoreaCityService{
	private final RoadKoreaCityRepository cityRepository;
	@Override
	public String findMappingCity(String address) {
		String mappedCity = null;

		switch (address) {
			case "경남":
				mappedCity = "경상남도";
				break;
			case "경북":
				mappedCity = "경상북도";
				break;
			case "전남":
				mappedCity = "전라남도";
				break;
			default:
				mappedCity = address;
		}

		return cityRepository.findComplaintRegionByCityName(mappedCity);
	}
}
