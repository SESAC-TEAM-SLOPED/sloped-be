package org.sesac.slopedbe.facilityreview.service;

import java.util.ArrayList;
import java.util.List;

import org.sesac.slopedbe.common.exception.BaseException;
import org.sesac.slopedbe.common.exception.GlobalErrorCode;
import org.sesac.slopedbe.facility.model.entity.Facility;
import org.sesac.slopedbe.facility.repository.FacilityRepository;
import org.sesac.slopedbe.facility.service.FacilityService;
import org.sesac.slopedbe.facilityreview.model.dto.FacilityReviewDTO;
import org.sesac.slopedbe.facilityreview.model.dto.FacilityReviewResponseDTO;
import org.sesac.slopedbe.facilityreview.model.entity.FacilityReview;
import org.sesac.slopedbe.facilityreview.repository.FacilityReviewRepository;
import org.sesac.slopedbe.member.exception.MemberErrorCode;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FacilityReviewService {
    private final FacilityRepository facilityRepository;
    private final FacilityReviewRepository facilityReviewRepository;
    private final FacilityService facilityService;
    private final MemberRepository memberRepository;

    // Service 목적: facility_report에 리뷰 저장, 리뷰 수정, 리뷰 조회, 리뷰 삭제 기능

    // 1. facility id로 Facility 검색 facilityService.getFacility //FacilityResponse로 반환
    // 2. Facility_review 테이블 저장
    // 3. Facility_review_image 테이블 저장

    public void createFacilityReview (MemberCompositeKey memberCompositeKey, FacilityReviewDTO facilityReviewDTO){
        Member member = memberRepository.findById(memberCompositeKey).orElseThrow(()->
            new MemberException(MemberErrorCode.MEMBER_ID_NOT_FOUND));
        Facility facility = facilityRepository.findById(facilityReviewDTO.getFacilityId()).orElseThrow(()->new BaseException(
            GlobalErrorCode.BAD_REQUEST));

        FacilityReview facilityReview = new FacilityReview(
            facilityReviewDTO.getIsConvenient(),
            facilityReviewDTO.getContext(),
            facility,
            member
        );
        facilityReviewRepository.save(facilityReview);
    }

    public List<FacilityReviewResponseDTO> readFacilityReview (MemberCompositeKey memberCompositeKey){
        List<FacilityReview> reviewEntities = facilityReviewRepository.findByMemberId(memberCompositeKey);
        List<FacilityReviewResponseDTO> reviews = new ArrayList<>();

        for (FacilityReview reviewEntity : reviewEntities) {
            reviews.add(FacilityReviewResponseDTO.toReviewResponseDTO(reviewEntity));
        }
        return reviews;
    }

}
