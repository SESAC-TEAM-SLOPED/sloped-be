package org.sesac.slopedbe.bookmark.service;

import java.util.List;
import java.util.stream.Collectors;

import org.sesac.slopedbe.bookmark.model.dto.request.FacilityTestDTO;
import org.sesac.slopedbe.bookmark.model.entity.Bookmark;
import org.sesac.slopedbe.bookmark.repository.BookmarkRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    public List<FacilityTestDTO> getBookmarks(Long facilityId) {
        List<Bookmark> bookmarks = bookmarkRepository.findAllByFacilityId(facilityId);
        return bookmarks.stream()
            .map(this::convertToFacilityTestDTO)
            .collect(Collectors.toList());
    }

    private FacilityTestDTO convertToFacilityTestDTO(Bookmark bookmark) {
		return new FacilityTestDTO(bookmark.getFacility().getId(), bookmark.getMember().getId().getEmail());
    }
}
