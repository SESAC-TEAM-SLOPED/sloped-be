package org.sesac.slopedbe.roadreport.s3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@RequiredArgsConstructor    // final 멤버변수가 있으면 생성자 항목에 포함시킴
@Component
@Service
public class S3UploadImages {
	private final S3Client amazonS3Client;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	// MultipartFile을 전달받아 File로 전환한 후 S3에 업로드
	// public String upload(MultipartFile multipartFile, String dirName) throws IOException {
	// 	File uploadFile = convert(multipartFile)
	// 		.orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
	// 	return upload(uploadFile, dirName);
	// }

	// private String upload(File uploadFile, String dirName) {
	// 	String fileName = dirName + "/" + uploadFile.getName();
	// 	String uploadImageUrl = putS3(uploadFile, fileName);
	//
	// 	removeNewFile(uploadFile);  // 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)
	//
	// 	return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
	// }

	public String putS3(byte[] uploadFile, String fileName) {

		amazonS3Client.putObject(PutObjectRequest.builder()
			.bucket(bucket)
			.key(fileName)
			.contentType("image/jpg")
			.build() , RequestBody.fromByteBuffer((ByteBuffer.wrap(uploadFile))));
		// amazonS3Client.putObject(
		// 	new PutObjectRequest(bucket, fileName, uploadFile)
		// 			// PublicRead 권한으로 업로드 됨
		// );
		return "sfaf";
	}

	private void removeNewFile(File targetFile) {
		if(targetFile.delete()) {
			log.info("파일이 삭제되었습니다.");
		}else {
			log.info("파일이 삭제되지 못했습니다.");
		}
	}

	private Optional<File> convert(MultipartFile file) throws  IOException {
		File convertFile = new File(file.getOriginalFilename());
		if(convertFile.createNewFile()) {
			try (FileOutputStream fos = new FileOutputStream(convertFile)) {
				fos.write(file.getBytes());
			}
			return Optional.of(convertFile);
		}
		return Optional.empty();
	}




}
