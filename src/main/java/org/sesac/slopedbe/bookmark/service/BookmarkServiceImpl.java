package org.sesac.slopedbe.bookmark.service;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.sesac.slopedbe.bookmark.model.entity.Bookmark;
import org.sesac.slopedbe.bookmark.model.entity.BookmarkId;
import org.sesac.slopedbe.bookmark.repository.BookmarkRepository;
import org.sesac.slopedbe.facility.model.entity.Facility;
import org.sesac.slopedbe.facility.repository.FacilityRepository;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final FacilityRepository facilityRepository;

    @Override
    public Bookmark addBookmark(String email, Long facilityId) {
        Member member = memberRepository
            .findById(1L)
            .orElseThrow(() -> new IllegalArgumentException("Invalid facility id: " + facilityId));
        Facility facility = facilityRepository
            .findById(facilityId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid facility id: " + facilityId));

        Bookmark bookmark = Bookmark.create(facility, member);

        return bookmarkRepository.save(bookmark);
    }

    @Override
    public void removeBookmark(String email, Long facilityId) {
        Member member = memberRepository
            .findById(1L)
            .orElseThrow(() -> new IllegalArgumentException("Invalid facility id: " + facilityId));
        Facility facility = facilityRepository
            .findById(facilityId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid facility id: " + facilityId));

        BookmarkId bookmarkId = new BookmarkId(facility, member);

        bookmarkRepository.deleteById(bookmarkId);
    }

    @Override
    public List<Bookmark> getBookmarksByMemberEmail(String email) {
        return bookmarkRepository.findByEmail(email);
    }
}
