package org.sesac.slopedbe.facility.model.entity;

import org.sesac.slopedbe.common.entity.BaseTimeEntity;
import org.sesac.slopedbe.member.model.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @JoinColumns({
        @JoinColumn(name = "email", referencedColumnName = "email"),
        @JoinColumn(name = "oauthType", referencedColumnName = "oauthType")
    })
    private Member member;
}