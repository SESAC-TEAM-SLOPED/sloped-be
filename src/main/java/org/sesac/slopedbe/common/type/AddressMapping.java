package org.sesac.slopedbe.common.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AddressMapping {
	SEOUL("서울", "서울특별시"),
	SEOULSI("서울시", "서울특별시"),
	GYEONGGI("경기", "경기도"),
	GYEONGNAM("경남", "경상남도"),
	GYEONGBUK("경북", "경상북도"),
	CHUNGNAM("충남", "충청남도"),
	CHUNGBUK("충북", "충청북도"),
	JEONNAM("전남", "전라남도"),
	JEONBUK("전북", "전북특별자치도"),
	JEJU("제주도", "제주특별자치도"),
	ULSAN("울산", "울산광역시"),
	DAEGU("대구", "대구광역시"),
	DAEJEON("대전", "대전광역시"),
	BUSAN("부산", "부산광역시");

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