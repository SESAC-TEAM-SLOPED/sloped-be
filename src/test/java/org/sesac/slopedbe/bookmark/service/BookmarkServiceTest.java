package org.sesac.slopedbe.bookmark.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.sesac.slopedbe.bookmark.model.entity.Bookmark;
import org.sesac.slopedbe.bookmark.model.entity.BookmarkId;
import org.sesac.slopedbe.bookmark.repository.BookmarkRepository;
import org.sesac.slopedbe.facility.model.entity.Facility;
import org.sesac.slopedbe.facility.model.type.FacilityType;
import org.sesac.slopedbe.facility.repository.FacilityRepository;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.memberenum.MemberRole;
import org.sesac.slopedbe.member.model.memberenum.MemberStatus;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({BookmarkServiceImpl.class})
public class BookmarkServiceTest {

	@Autowired
	private BookmarkService bookmarkService;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private FacilityRepository facilityRepository;
	@Autowired
	private BookmarkRepository bookmarkRepository;

	@BeforeEach
	public void setUp() {
		System.out.println("setup........");
		Member member = new Member();
		member.setEmail("example@email.com");
		member.setNickname("김신입");
		member.setMemberStatus(MemberStatus.ACTIVE);
		member.setMemberRole(MemberRole.USER);
		member.setCreatedAt(LocalDateTime.now());
		member.setUpdatedAt(LocalDateTime.now());
		memberRepository.save(member);

		Facility facility = new Facility("", true, true, "", 3D, 3D, "", "", FacilityType.CAFE, "", true);
		facilityRepository.save(facility);

	}

	@AfterEach
	public void afterEach() {
		System.out.println("deleteAll........");
		bookmarkRepository.deleteAll();
	}

	@Test
	public void addBookmarkTest() {
		System.out.println("addBookmarkTest........");
		Member member = memberRepository.findByEmail("example@email.com").get();
		Facility facility = facilityRepository.findById(1L).get();

		bookmarkService.addBookmark("example@email.com", 1L);

		BookmarkId bookmarkId = new BookmarkId(facility, member);

		assertTrue(bookmarkRepository.findById(bookmarkId).isPresent());
	}

	@Test
	public void removeBookmarkTest() {
		System.out.println("removeBookmarkTest........");
		Member member = memberRepository.findByEmail("example@email.com").get();
		Facility facility = facilityRepository.findById(1L).get();



		bookmarkService.removeBookmark("example@email.com", 1L);

		BookmarkId bookmarkId = new BookmarkId(facility, member);
		assertTrue(bookmarkRepository.findById(bookmarkId).isEmpty());
	}

	@Test
	public void getBookmarksByMemberEmailTest() {
		System.out.println("getBookmarksByMemberEmailTest........");
		bookmarkService.addBookmark("example@email.com", 1L);
		List<Bookmark> bookmarks = bookmarkService.getBookmarksByMemberEmail("example@email.com");

		assertThat(bookmarks.size()).isEqualTo(1);
	}
}
