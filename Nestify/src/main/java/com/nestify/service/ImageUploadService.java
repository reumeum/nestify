package com.nestify.service;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface ImageUploadService {

    /**
     * MultipartFile 형식의 이미지를 S3에 업로드
     *
     * @param image 업로드할 이미지
     * @return 업로드된 이미지의 S3 URL
     * @throws IOException 파일 업로드 중 발생하는 예외
     */
    String upload(MultipartFile image, String originalFileName) throws IOException;

    /**
     * File 형식의 이미지를 S3에 업로드
     *
     * @param image 업로드할 이미지 파일
     * @param originalFileName 원본 파일명
     * @return 업로드된 이미지의 S3 URL
     * @throws IOException 파일 업로드 중 발생하는 예외
     */
    String upload(File image, String originalFileName) throws IOException;
    
    /**
     * 파일 삭제
     *
     * @param objectKey 파일 경로
     * @throws IOException 파일 업로드 중 발생하는 예외
     */
	void delete(String objectKey) throws IOException;
}
