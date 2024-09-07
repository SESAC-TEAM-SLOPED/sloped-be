package org.sesac.slopedbe.facility.model.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sesac.slopedbe.common.entity.BaseTimeEntity;
import org.sesac.slopedbe.member.model.entity.Member;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(FacilityRegistrarId.class)
@Table(name = "facility_registrar")
@Entity
public class FacilityRegistrar extends BaseTimeEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Id
    @ManyToOne
    @JoinColumn(name = "email", nullable = false)
    private Member member;
}
