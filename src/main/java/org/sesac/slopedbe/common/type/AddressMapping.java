package org.sesac.slopedbe.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AddressMapping {
	GYEONGNAM("경남", "경상남도"),
	GYEONGBUK("경북", "경상북도"),
	JEONNAM("전남", "전라남도");

	private final String address;
	private final String mappedCity;

	public static String getMappedCity(String address) {
		for (AddressMapping mapping : values()) {
			if (mapping.getAddress().equals(address)) {
				return mapping.getMappedCity();
			}
		}
		return address.split(" ")[0]; // 기본값 반환
	}
}