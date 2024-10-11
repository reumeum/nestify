package com.nestify.service.impl;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.nestify.service.ImageUploadService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ImageUploadServiceImpl implements ImageUploadService {

	private final AmazonS3 s3;

	@Value("${s3.bucket}")
	private String bucket;

	public String upload(MultipartFile image, String originalFileName) throws IOException {
		/* 업로드할 파일의 이름을 변경 */
		String fileName = changeFileName(originalFileName);

		/* s3에 업로드할 파일의 메타데이터 생성 */
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(image.getContentType());
		metadata.setContentLength(image.getSize());

		/* s3에 파일 업로드 */
		s3.putObject(bucket, fileName, image.getInputStream(), metadata);

		/* 업로드한 파일의 s3 url 주소 반환 */
		return s3.getUrl(bucket, fileName).toString();
	}
	
	public String upload(File image, String originalFileName) throws IOException {
		/* 업로드할 파일의 이름을 변경 */
		String fileName = changeFileName(originalFileName);
		
		/* s3에 파일 업로드 */
		s3.putObject(bucket, fileName, image);
		
		/* 업로드한 파일의 s3 url 주소 반환 */
		return s3.getUrl(bucket, fileName).toString();
	}

	public void delete(String objectKey) throws IOException {
		s3.deleteObject(bucket, objectKey);
	}

	private String changeFileName(String originalFileName) {
		String extension = getExtension(originalFileName);
		String fileNameWithoutExtension = getFileNameWithoutExtension(originalFileName);

		String uniqueFileName = fileNameWithoutExtension + "_"
				+ new Timestamp(System.currentTimeMillis()).toString().replaceAll(" ", "_").replaceAll(":", "-")
				+ (extension != null ? "." + extension : "");

		return uniqueFileName;
	}

	/* 파일 확장자를 추출하는 메서드 */
	private static String getExtension(String fileName) {
		if (fileName == null || fileName.lastIndexOf(".") == -1) {
			return null;
		}
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	/* 파일 이름을 추출하는 메서드 */
	private static String getFileNameWithoutExtension(String fileName) {
		if (fileName == null || fileName.lastIndexOf(".") == -1) {
			return null;
		}
		return fileName.substring(0, fileName.lastIndexOf("."));
	}
}
