package org.sesac.slopedbe.bookmark.service;

import java.util.ArrayList;
import java.util.List;

import org.sesac.slopedbe.bookmark.model.dto.BookmarkRequestDTO;
import org.sesac.slopedbe.bookmark.model.dto.BookmarkResponseDTO;
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
    public void addBookmark(String email, BookmarkRequestDTO bookmarkRequestDTO) {
        System.out.println(bookmarkRequestDTO.getFacilityId());
        Member member = memberRepository.findByEmail(email).orElse(null);
        Facility facility = facilityRepository.findById(bookmarkRequestDTO.getFacilityId()).orElse(null);

        // 중복 제거

        System.out.println("facilityId............" + facility);
        Bookmark bookmark = Bookmark.create(facility, member);

        bookmarkRepository.save(bookmark);
    }

    @Override
    public void removeBookmark(String email, BookmarkRequestDTO bookmarkRequestDTO) {
        Member member = memberRepository.findByEmail(email).orElse(null);
        Facility facility = facilityRepository.findById(bookmarkRequestDTO.getFacilityId()).orElse(null);

        BookmarkId bookmarkId = new BookmarkId(facility, member);
        System.out.println(bookmarkId);
        bookmarkRepository.deleteById(bookmarkId);
    }

    @Override
    public List<BookmarkResponseDTO> getBookmarksByEmail(String email) {
        List<Bookmark> bookmarkEntities = bookmarkRepository.findByMember_Email(email);
        List<BookmarkResponseDTO> bookmarks = new ArrayList<>();

        for (Bookmark bookmarkEntity : bookmarkEntities) {
            bookmarks.add(BookmarkResponseDTO.toBookmarkDTO(bookmarkEntity));
        }

        return bookmarks;
    }
}
