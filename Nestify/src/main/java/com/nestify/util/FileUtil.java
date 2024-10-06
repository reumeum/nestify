package com.nestify.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class FileUtil {
	//기본 저장 경로 (상대 경로)
	private static final String BASE_DIRECTORY = "/images/";

	//파일 저장 유틸리티
	public static String saveFile(MultipartFile file, Long userId, String directory) throws IOException {

		if (file == null || file.isEmpty()) {
			return null;
		}
		
		String relativePath = "/images/" + userId + "/" + directory;

		//저장할 폴더 경로 생성
		Path dirPath = Paths.get("src/main/resources/static" + relativePath);

		if (!Files.exists(dirPath)) {
			Files.createDirectories(dirPath); //디렉토리가 존재하지 않으면 생성
		}

		//파일이름 설정
		String orifinalFileName = file.getOriginalFilename();
		String extension = getExtension(orifinalFileName);
		String uniqueFileName = UUID.randomUUID().toString() + (extension != null ? "." + extension : "");

		//파일 경로
		Path filePath = dirPath.resolve(uniqueFileName);

		//파일을 경로에 저장
		Files.copy(file.getInputStream(), filePath);

		//저장된 파일의 상대경로 반환
		return relativePath + "/" + uniqueFileName;

	}

	// 커버 이미지 저장 메서드
	public static String saveCoverImg(MultipartFile file, Long userId) throws IOException {
		return saveFile(file, userId, "cover-images");
	}

	// 파일 확장자를 추출하는 메서드
	private static String getExtension(String fileName) {
		if (fileName == null || fileName.lastIndexOf(".") == -1) {
			return null;
		}
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}
}
