package org.sesac.slopedbe.bookmark.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.sesac.slopedbe.bookmark.exception.BookmarkErrorCode;
import org.sesac.slopedbe.bookmark.exception.BookmarkException;
import org.sesac.slopedbe.bookmark.model.dto.BookmarkRequestDTO;
import org.sesac.slopedbe.bookmark.model.dto.BookmarkResponseDTO;
import org.sesac.slopedbe.bookmark.model.entity.Bookmark;
import org.sesac.slopedbe.bookmark.model.entity.BookmarkId;
import org.sesac.slopedbe.bookmark.repository.BookmarkRepository;
import org.sesac.slopedbe.common.exception.BaseException;
import org.sesac.slopedbe.common.exception.GlobalErrorCode;
import org.sesac.slopedbe.facility.model.entity.Facility;
import org.sesac.slopedbe.facility.repository.FacilityRepository;
import org.sesac.slopedbe.member.exception.MemberErrorCode;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookmarkServiceImpl implements BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final MemberRepository memberRepository;
    private final FacilityRepository facilityRepository;

    @Override
    public void addBookmark(MemberCompositeKey memberCompositeKey, BookmarkRequestDTO bookmarkRequestDTO) {
        Member member = memberRepository.findById(memberCompositeKey).orElseThrow(()->
            new MemberException(MemberErrorCode.MEMBER_ID_NOT_FOUND));
        Facility facility = facilityRepository.findById(bookmarkRequestDTO.getFacilityId()).orElseThrow(()->new BaseException(
            GlobalErrorCode.BAD_REQUEST));

        BookmarkId bookmarkId = new BookmarkId(facility, member);

        Optional<Bookmark> bookmark = bookmarkRepository.findById(bookmarkId);

        if(bookmark.isPresent()) {
            throw new BookmarkException(BookmarkErrorCode.BOOKMARK_ALREADY_EXISTS);
        }

        Bookmark newBookmark = Bookmark.create(facility, member);

        bookmarkRepository.save(newBookmark);
    }

    @Override
    public void removeBookmark(MemberCompositeKey memberCompositeKey, Long facilityId) {
        Member member = memberRepository.findById(memberCompositeKey).orElseThrow(()->
            new MemberException(MemberErrorCode.MEMBER_ID_NOT_FOUND));
        Facility facility = facilityRepository.findById(facilityId).orElseThrow(()->new BaseException(
            GlobalErrorCode.BAD_REQUEST));

        BookmarkId bookmarkId = new BookmarkId(facility, member);

        bookmarkRepository.deleteById(bookmarkId);
    }

    @Override
    public List<BookmarkResponseDTO> getBookmarksById(MemberCompositeKey memberCompositeKey) {
        List<Bookmark> bookmarkEntities = bookmarkRepository.findByMemberId(memberCompositeKey);
        List<BookmarkResponseDTO> bookmarks = new ArrayList<>();

        for (Bookmark bookmarkEntity : bookmarkEntities) {
            bookmarks.add(BookmarkResponseDTO.toBookmarkDTO(bookmarkEntity));
        }

        return bookmarks;
    }
}
