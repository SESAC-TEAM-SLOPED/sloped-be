package org.sesac.slopedbe.bookmark.model.dto;

import org.sesac.slopedbe.bookmark.model.entity.Bookmark;
import org.sesac.slopedbe.facility.model.type.FacilityType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookmarkResponseDTO {
	private Long facilityId;
	private String name;
	private FacilityType facilityType;
	private String address;
	private int countOfReviews;

	public static BookmarkResponseDTO toBookmarkDTO(Bookmark bookmark) {
		BookmarkResponseDTO bookmarkResponseDTO = new BookmarkResponseDTO();
		bookmarkResponseDTO.setFacilityId(bookmark.getFacility().getId());
		bookmarkResponseDTO.setName(bookmark.getFacility().getName());
		bookmarkResponseDTO.setFacilityType(bookmark.getFacility().getFacilityType());
		bookmarkResponseDTO.setAddress(bookmark.getFacility().getAddress());
		bookmarkResponseDTO.setCountOfReviews(bookmark.getFacility().getFacilityReviews().size());

		return bookmarkResponseDTO;
	}
}
