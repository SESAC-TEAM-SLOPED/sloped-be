package org.sesac.slopedbe.bookmark.model.entity;

import java.time.LocalDateTime;

import org.sesac.slopedbe.common.entity.BaseTimeEntity;
import org.sesac.slopedbe.facility.model.entity.Facility;
import org.sesac.slopedbe.member.model.entity.Member;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public static Bookmark create(Facility facility, Member member) {
        Bookmark bookmark = new Bookmark();
        bookmark.facility = facility;
        bookmark.member = member;
        bookmark.createdAt = LocalDateTime.now();
        bookmark.updatedAt = LocalDateTime.now();
        return bookmark;
    }
}
