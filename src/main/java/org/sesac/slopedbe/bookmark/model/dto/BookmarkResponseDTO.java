package org.sesac.slopedbe.bookmark.model.dto;

import java.math.BigDecimal;

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
	private BigDecimal latitude;
	private BigDecimal longitude;
	private int countOfReviews;

	public static BookmarkResponseDTO toBookmarkDTO(Bookmark bookmark) {
		return new BookmarkResponseDTO(
			bookmark.getFacility().getId(),
			bookmark.getFacility().getName(),
			bookmark.getFacility().getFacilityType(),
			bookmark.getFacility().getAddress(),
			bookmark.getFacility().getLatitude(),
			bookmark.getFacility().getLongitude(),
			bookmark.getFacility().getFacilityReviews().size()
		);
	}
}
