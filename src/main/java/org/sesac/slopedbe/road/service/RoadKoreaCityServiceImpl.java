package org.sesac.slopedbe.road.service;

import org.sesac.slopedbe.common.type.AddressMapping;
import org.sesac.slopedbe.road.repository.RoadKoreaCityRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RoadKoreaCityServiceImpl  implements RoadKoreaCityService{
	private final RoadKoreaCityRepository cityRepository;
	@Override
	public String findMappingCity(String address) {
		// common/type에 있는 AddressMapping enum을 사용하여 매핑된 도시 이름을 변경해줍니다.
		String mappedCity = AddressMapping.getMappedCity(address);

		return cityRepository.findComplaintRegionByCityName(mappedCity);
	}
}
