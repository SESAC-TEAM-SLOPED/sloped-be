package org.sesac.slopedbe.bookmark.model.entity;

import org.sesac.slopedbe.common.entity.BaseTimeEntity;
import org.sesac.slopedbe.facility.model.entity.Facility;
import org.sesac.slopedbe.member.model.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(BookmarkId.class)
@Table(name = "bookmark")
@Entity
public class Bookmark extends BaseTimeEntity {
    @Id
    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Id
    @ManyToOne
    @JoinColumn(name = "email", nullable = false)
    private Member member;

    public static Bookmark create(Facility facility, Member member) {
        Bookmark bookmarkEntity = new Bookmark();
        bookmarkEntity.facility = facility;
        bookmarkEntity.member = member;

        return bookmarkEntity;
    }
}
