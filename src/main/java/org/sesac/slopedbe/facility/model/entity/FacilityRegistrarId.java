package org.sesac.slopedbe.facility.model.entity;

import java.io.Serializable;

import org.sesac.slopedbe.member.model.entity.Member;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FacilityRegistrarId implements Serializable {
    private Facility facility;
    private Member member;
}
