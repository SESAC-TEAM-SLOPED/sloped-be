package org.sesac.slopedbe.roadreport.s3;

import java.io.IOException;
import java.io.InputStream;

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


	public String upload(MultipartFile multipartFile, String dirName, String saveFileName) throws IOException {
		String fileName = dirName + "/" + saveFileName;
		return uploadFile(multipartFile, fileName);
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
