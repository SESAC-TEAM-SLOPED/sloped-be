package org.sesac.slopedbe.facilityreview.service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.sesac.slopedbe.common.exception.BaseException;
import org.sesac.slopedbe.common.exception.GlobalErrorCode;
import org.sesac.slopedbe.facility.model.entity.Facility;
import org.sesac.slopedbe.facility.repository.FacilityRepository;
import org.sesac.slopedbe.facilityreview.exception.FacilityReviewErrorCode;
import org.sesac.slopedbe.facilityreview.exception.FacilityReviewException;
import org.sesac.slopedbe.facilityreview.model.dto.FacilityReviewRequestDTO;
import org.sesac.slopedbe.facilityreview.model.dto.FacilityReviewResponseDTO;
import org.sesac.slopedbe.facilityreview.model.entity.FacilityReview;
import org.sesac.slopedbe.facilityreview.model.entity.FacilityReviewImage;
import org.sesac.slopedbe.facilityreview.repository.FacilityReviewImageRepository;
import org.sesac.slopedbe.facilityreview.repository.FacilityReviewRepository;
import org.sesac.slopedbe.member.exception.MemberErrorCode;
import org.sesac.slopedbe.member.exception.MemberException;
import org.sesac.slopedbe.member.model.entity.Member;
import org.sesac.slopedbe.member.model.entity.MemberCompositeKey;
import org.sesac.slopedbe.member.repository.MemberRepository;
import org.sesac.slopedbe.roadreport.s3.S3UploadImages;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FacilityReviewService {
    private final FacilityRepository facilityRepository;
    private final FacilityReviewRepository facilityReviewRepository;
    private final MemberRepository memberRepository;
    private final S3UploadImages s3UploadImages;
    private final FacilityReviewImageRepository facilityReviewImageRepository;

    @Value("${REVIEW_DIR}")
    private String reviewImageDir;

    public void createFacilityReview (MemberCompositeKey memberCompositeKey, Long facilityId, FacilityReviewRequestDTO facilityReviewDTO){
        Member member = memberRepository.findById(memberCompositeKey).orElseThrow(()->
            new MemberException(MemberErrorCode.MEMBER_ID_NOT_FOUND));
        Facility facility = facilityRepository.findById(facilityId).orElseThrow(()->new BaseException(
            GlobalErrorCode.BAD_REQUEST));

        FacilityReview facilityReview = new FacilityReview(
            facilityReviewDTO.getIsConvenient(),
            facilityReviewDTO.getContent(),
            facility,
            member
        );
        facilityReviewRepository.save(facilityReview);

        try {
            createFacilityReviewImages(facilityReviewDTO.getFiles(), facilityReview);
        } catch (IOException e) {
            throw new FacilityReviewException(FacilityReviewErrorCode.GENERAL_ERROR);
        }
    }

    public List<FacilityReviewResponseDTO> readAllFacilityReviews(MemberCompositeKey memberCompositeKey) {
        List<FacilityReview> reviewEntities = facilityReviewRepository.findByMemberId(memberCompositeKey);
        List<FacilityReviewResponseDTO> reviews = new ArrayList<>();

        for (FacilityReview reviewEntity : reviewEntities) {
            List<String> urls = readFacilityReviewImages(reviewEntity.getId());
            reviews.add(FacilityReviewResponseDTO.toReviewResponseDTO(reviewEntity, urls));
        }
        return reviews;
    }

    public void updateFacilityReview(Long facilityReviewId, FacilityReviewRequestDTO facilityReviewRequestDTO){
        Optional<FacilityReview> facilityReview = facilityReviewRepository.findById(facilityReviewId);
        if(facilityReview.isEmpty()) {
            throw new FacilityReviewException(FacilityReviewErrorCode.FACILITY_REVIEW_NOT_FOUND);
        }
        FacilityReview existingFacilityReview = facilityReview.get();
        existingFacilityReview.setContent(facilityReviewRequestDTO.getContent());
        facilityReviewRepository.save(existingFacilityReview);

        //기존 image 모두 제거
        List<FacilityReviewImage> reviewImageList = facilityReviewImageRepository.findByFacilityReviewId(facilityReviewId);

        for (FacilityReviewImage facilityReviewImage : reviewImageList) {
            s3UploadImages.deleteFile(facilityReviewImage.getUrl());
            facilityReviewImageRepository.deleteById(facilityReviewImage.getUrl());
        }

        //새로운 이미지 등록
        try {
            createFacilityReviewImages(facilityReviewRequestDTO.getFiles(), existingFacilityReview);
        } catch (IOException e) {
            throw new FacilityReviewException(FacilityReviewErrorCode.GENERAL_ERROR);
        }
    }

    public void deleteFacilityReview(Long facilityReviewId){
        Optional<FacilityReview> facilityReview = facilityReviewRepository.findById(facilityReviewId);
        if(facilityReview.isEmpty()) {
            throw new FacilityReviewException(FacilityReviewErrorCode.FACILITY_REVIEW_NOT_FOUND);
        }

        List<FacilityReviewImage> reviewImageList = facilityReviewImageRepository.findByFacilityReviewId(facilityReviewId);

        for (FacilityReviewImage facilityReviewImage : reviewImageList) {
            s3UploadImages.deleteFile(facilityReviewImage.getUrl());
            facilityReviewImageRepository.deleteById(facilityReviewImage.getUrl());
        }

        facilityReviewRepository.deleteById(facilityReviewId);
    }

    private void createFacilityReviewImages(List<MultipartFile> files, FacilityReview facilityReview) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        if (files == null || files.isEmpty()) {
            return;
        }

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (!file.getContentType().startsWith("image")) {
                throw new FacilityReviewException(FacilityReviewErrorCode.REVIEW_IMAGE_UPLOAD_FAILED);
            }

            String timestamp = dateFormat.format(new Date());
            String saveFileName = timestamp + "_" + file.getOriginalFilename();
            String fileUrl = s3UploadImages.upload(file, reviewImageDir, saveFileName);

            FacilityReviewImage facilityReviewImage = new FacilityReviewImage(
                fileUrl,
                facilityReview
            );

            facilityReviewImageRepository.save(facilityReviewImage);
        }
    }

    private List<String> readFacilityReviewImages(Long facilityReviewId) {
        List<FacilityReviewImage> reviewImageList = facilityReviewImageRepository.findByFacilityReviewId(facilityReviewId);

        return reviewImageList.stream()
            .map(FacilityReviewImage::getUrl)
            .collect(Collectors.toList());
    }

}
