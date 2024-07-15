package org.sesac.slopedbe.bookmark.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sesac.slopedbe.bookmark.model.dto.BookmarkRequestDTO;
import org.sesac.slopedbe.bookmark.model.dto.BookmarkResponseDTO;
import org.sesac.slopedbe.bookmark.model.entity.BookmarkId;
import org.sesac.slopedbe.bookmark.repository.BookmarkRepository;
import org.sesac.slopedbe.facility.model.entity.Facility;
import org.sesac.slopedbe.facility.repository.FacilityRepository;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.memberenum.MemberRole;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class BookmarkServiceTest {

	@Autowired
	private BookmarkService bookmarkService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private FacilityRepository facilityRepository;

	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	public void setUp() {
		System.out.println("setup........");
		Member testMember = new Member(
			"test@example.com",
			"김갑생",
			false,
			MemberStatus.ACTIVE,
			MemberRole.ADMIN,
			null, // refreshToken
			null, // oauthType
			"testId",
			passwordEncoder.encode("plainTextPassword"),
			null, // socialAuthCode
			null // socialOauthType
		);
		memberRepository.save(testMember);
	}

	@AfterEach
	public void afterEach() {
		System.out.println("deleteAll........");
		bookmarkRepository.deleteAll();
	}

	@Test
	public void addBookmarkTest() throws IOException {
		System.out.println("addBookmarkTest........");
		Member member = memberRepository.findByEmail("example@email.com").orElse(null);
		Facility facility = facilityRepository.findById(1L).orElse(null);

		BookmarkRequestDTO bookmarkRequestDTO = new BookmarkRequestDTO();
		bookmarkRequestDTO.setFacilityId(facility.getId());

		bookmarkService.addBookmark("example@email.com", bookmarkRequestDTO);

		BookmarkId bookmarkId = new BookmarkId(facility, member);

		assertTrue(bookmarkRepository.findById(bookmarkId).isPresent());
	}

	@Test
	public void removeBookmarkTest() {
		System.out.println("removeBookmarkTest........");
		Member member = memberRepository.findByEmail("example@email.com").get();
		Facility facility = facilityRepository.findById(1L).get();

		BookmarkRequestDTO bookmarkRequestDTO = new BookmarkRequestDTO();
		bookmarkRequestDTO.setFacilityId(facility.getId());

		bookmarkService.removeBookmark("example@email.com", bookmarkRequestDTO);

		BookmarkId bookmarkId = new BookmarkId(facility, member);
		assertTrue(bookmarkRepository.findById(bookmarkId).isEmpty());
	}

	@Test
	public void getBookmarksByMemberEmailTest() {
		System.out.println("getBookmarksByMemberEmailTest........");

		List<BookmarkResponseDTO> bookmarks = bookmarkService.getBookmarksByMemberEmail("example@email.com");

		assertThat(bookmarks.size()).isEqualTo(1);
	}
}
