package org.sesac.slopedbe.facility.model.type;

import lombok.Getter;

@Getter
public enum FacilityType {
    RESTAURANT("식당"),
    CAFE("카페"),
    PUBLIC_SPACE("공공장소"),
    ETC("기타");

    private final String name;

    FacilityType(String name) {
        this.name = name;
    }
}
