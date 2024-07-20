package org.sesac.slopedbe.bookmark.service;

import java.util.List;

import org.sesac.slopedbe.bookmark.model.entity.Bookmark;
import org.sesac.slopedbe.bookmark.model.entity.BookmarkId;
import org.sesac.slopedbe.bookmark.repository.BookmarkRepository;
import org.sesac.slopedbe.facility.model.entity.Facility;
import org.sesac.slopedbe.facility.repository.FacilityRepository;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final FacilityRepository facilityRepository;

    @Override
    public void addBookmark(String email, Long facilityId) {

        Member member = memberRepository.findByEmail(email).orElse(null);
        Facility facility = facilityRepository.findById(facilityId).orElse(null);

        // 중복 제거

        System.out.println("facilityId............" + facility);
        Bookmark bookmark = Bookmark.create(facility, member);

        bookmarkRepository.save(bookmark);
    }

    @Override
    public void removeBookmark(String email, Long facilityId) {
        Member member = memberRepository.findByEmail(email).orElse(null);
        Facility facility = facilityRepository.findById(facilityId).orElse(null);

        BookmarkId bookmarkId = new BookmarkId(facility, member);

        bookmarkRepository.deleteById(bookmarkId);
    }

    @Override
    public List<Bookmark> getBookmarksByEmail(String email) {
        Member member = memberRepository.findByEmail(email).orElse(null);

        return bookmarkRepository.findByMember_Email(email);
    }
}
