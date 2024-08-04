package org.sesac.slopedbe.roadreport.s3;

import java.io.IOException;
import java.io.InputStream;

import org.sesac.slopedbe.roadreport.exception.RoadReportErrorCode;
import org.sesac.slopedbe.roadreport.exception.RoadReportException;
import org.sesac.slopedbe.roadreport.s3.exception.FileSizeLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
@Service
public class S3UploadImages {
	private final AmazonS3 amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${file.max-size:20971520}")  // 기본값을 20MB로 설정
	private long maxFileSize;


	public String upload(MultipartFile multipartFile, String dirName, String saveFileName) throws IOException {
		validateFile(multipartFile); // 파일 크기, 형식 제한

		String fileName = dirName + "/" + saveFileName;
		return uploadFile(multipartFile, fileName);
	}

	private void validateFile(MultipartFile multipartFile) {
		if (multipartFile.getSize() > maxFileSize) {
			throw new FileSizeLimitExceededException("파일 크기 제한을 초과했습니다. 최대 허용 크기: " + (maxFileSize / 1024 / 1024) + "MB");
		}

		String contentType = multipartFile.getContentType();
		if (!(contentType.equals("image/png") || contentType.equals("image/jpeg"))) {
			throw new RoadReportException(RoadReportErrorCode.REPORT_IMAGE_UPLOAD_FAILED);
		}
	}

	private String uploadFile(MultipartFile multipartFile, String fileName) throws IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(multipartFile.getSize());
		metadata.setContentType(multipartFile.getContentType());

		try (InputStream inputStream = multipartFile.getInputStream()) {
			amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, inputStream, metadata)
				.withCannedAcl(CannedAccessControlList.PublicRead)); // PublicRead 권한으로 업로드
		} catch (IOException e) {
			log.error("S3에 파일 업로드 중 오류 발생: {}", e.getMessage(), e);
			throw e;
		}

		return amazonS3Client.getUrl(bucket, fileName).toString();
	}
}
