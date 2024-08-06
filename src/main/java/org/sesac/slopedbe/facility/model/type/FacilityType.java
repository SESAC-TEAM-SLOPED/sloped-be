package org.sesac.slopedbe.facility.model.type;

import org.sesac.slopedbe.facility.exception.FacilityErrorCode;
import org.sesac.slopedbe.facility.exception.FacilityException;

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

    public static String getDisplayNameByValue(String value) {
        try {
            return valueOf(value).getName();
        } catch (IllegalArgumentException e) {
            throw new FacilityException(FacilityErrorCode.FACILITY_TYPE_NOT_FOUND);
        }
    }
}
