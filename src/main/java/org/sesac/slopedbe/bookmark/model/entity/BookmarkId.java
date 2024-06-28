package org.sesac.slopedbe.bookmark.model.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.sesac.slopedbe.facility.model.entity.Facility;
import org.sesac.slopedbe.member.model.entity.Member;

import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class BookmarkId implements Serializable {
    private Facility facility;
    private Member member;
}
