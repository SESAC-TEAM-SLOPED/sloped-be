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
import org.sesac.slopedbe.facilityreview.model.dto.FacilityReviewDTO;
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

    public void createFacilityReview (MemberCompositeKey memberCompositeKey, FacilityReviewDTO facilityReviewDTO){
        Member member = memberRepository.findById(memberCompositeKey).orElseThrow(()->
            new MemberException(MemberErrorCode.MEMBER_ID_NOT_FOUND));
        Facility facility = facilityRepository.findById(facilityReviewDTO.getFacilityId()).orElseThrow(()->new BaseException(
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

    public List<FacilityReviewResponseDTO> readFacilityReview (MemberCompositeKey memberCompositeKey){
        List<FacilityReview> reviewEntities = facilityReviewRepository.findByMemberId(memberCompositeKey);
        List<FacilityReviewResponseDTO> reviews = new ArrayList<>();

        for (FacilityReview reviewEntity : reviewEntities) {
            reviews.add(FacilityReviewResponseDTO.toReviewResponseDTO(reviewEntity));
        }
        return reviews;
    }

    public List<String> readFacilityReviewImages(Long facilityReviewId) {
        List<FacilityReviewImage> reviewImageList = facilityReviewImageRepository.findByFacilityReviewId(facilityReviewId);

        List<String> urls = reviewImageList.stream()
            .map(FacilityReviewImage::getUrl)
            .collect(Collectors.toList());

        return urls;
    }

    public void updateFacilityReview(FacilityReviewDTO facilityReviewDTO){
        Facility facility = facilityRepository.findById(facilityReviewDTO.getFacilityId()).orElseThrow(()->
            new BaseException(GlobalErrorCode.BAD_REQUEST));

        Optional<FacilityReview> facilityReview = facilityReviewRepository.findById(facilityReviewDTO.getFacilityReviewId());

        if(facilityReview.isEmpty()) {
            throw new FacilityReviewException(FacilityReviewErrorCode.FACILITY_REVIEW_NOT_FOUND);
        }
        FacilityReview existingFacilityReview = facilityReview.get();
        existingFacilityReview.setContent(facilityReviewDTO.getContent());
        facilityReviewRepository.save(existingFacilityReview);
    }

    public void updateFacilityReviewImages(FacilityReviewDTO facilityReviewDTO){
        Optional<FacilityReview> facilityReview = facilityReviewRepository.findById(facilityReviewDTO.getFacilityReviewId());

        if(facilityReview.isEmpty()) {
            throw new FacilityReviewException(FacilityReviewErrorCode.FACILITY_REVIEW_NOT_FOUND);
        }

        FacilityReview existingFacilityReview = facilityReview.get();
        List<FacilityReviewImage> reviewImageList = facilityReviewImageRepository.findByFacilityReviewId(facilityReviewDTO.getFacilityReviewId());

        for (FacilityReviewImage facilityReviewImage : reviewImageList) {
            s3UploadImages.deleteFile(facilityReviewImage.getUrl());
            facilityReviewImageRepository.deleteById(facilityReviewImage.getUrl());
        }

        try {
            createFacilityReviewImages(facilityReviewDTO.getFiles(), existingFacilityReview);
        } catch (IOException e) {
            throw new FacilityReviewException(FacilityReviewErrorCode.GENERAL_ERROR);
        }

    }

    public void deleteFacilityReview(FacilityReviewDTO facilityReviewDTO){
        Optional<FacilityReview> facilityReview = facilityReviewRepository.findById(facilityReviewDTO.getFacilityReviewId());

        if(facilityReview.isEmpty()) {
            throw new FacilityReviewException(FacilityReviewErrorCode.FACILITY_REVIEW_NOT_FOUND);
        }

        List<FacilityReviewImage> reviewImageList = facilityReviewImageRepository.findByFacilityReviewId(facilityReviewDTO.getFacilityReviewId());

        for (FacilityReviewImage facilityReviewImage : reviewImageList) {
            s3UploadImages.deleteFile(facilityReviewImage.getUrl());
            facilityReviewImageRepository.deleteById(facilityReviewImage.getUrl());
        }

        facilityReviewRepository.deleteById(facilityReviewDTO.getFacilityReviewId());
    }

    private void createFacilityReviewImages(List<MultipartFile> files, FacilityReview facilityReview) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

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

}
