package com.nestify.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.nestify.service.ImageUploadService;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MetaTagParser {
	
	private final ImageUploadService imageUploadService;

	/**
	 * URL로부터 메타태그를 파싱
	 *
	 * @param url 메타태그를 파싱할 웹 페이지의 URL
	 * @return 파싱된 메타태그의 맵 (키: 메타태그 이름, 값: 메타태그 내용)
	 * @throws IOException URL 연결 오류 발생 시 예외 처리
	 */
	public Map<String, String> parseMetaTags(String url) throws IOException {
		Map<String, String> metaTags = new HashMap<>();

		try {
			Document doc = Jsoup.connect(url).get();

			// 기본 <title> 태그 추가
			metaTags.put("title", doc.title());

			// Open Graph (OG) 메타태그 파싱
			addMetaTag(metaTags, doc, "og:title");
			addMetaTag(metaTags, doc, "og:description");
			addMetaTag(metaTags, doc, "og:image");
			addMetaTag(metaTags, doc, "og:url");

			// Twitter 메타태그 파싱
			addMetaTag(metaTags, doc, "twitter:title");
			addMetaTag(metaTags, doc, "twitter:description");
			addMetaTag(metaTags, doc, "twitter:image");

			// 기본 메타태그 파싱
			addMetaTag(metaTags, doc, "description");

			return metaTags;

		} catch (HttpStatusException e) {
			if (e.getStatusCode() == 403) {
				metaTags.put("title", "Untitled");
			}

			return metaTags;
		}

	}

	/**
	 * 특정 메타태그를 파싱하여 맵에 추가
	 *
	 * @param metaTags 메타태그를 저장할 맵
	 * @param doc      Jsoup 문서 객체
	 * @param metaName 파싱할 메타태그의 이름 또는 속성 값 (예: "og:title", "description")
	 */
	private void addMetaTag(Map<String, String> metaTags, Document doc, String metaName) {
		Element metaElement = doc.selectFirst("meta[property=" + metaName + "]");
		if (metaElement == null) {
			metaElement = doc.selectFirst("meta[name=" + metaName + "]");
		}
		if (metaElement != null) {
			metaTags.put(metaName, metaElement.attr("content"));
		}
	}

	/**
	 * 우선순위를 고려하여 메타태그 값을 선택합니다.
	 * 
	 * @param metaTags 메타태그 맵
	 * @param keys     우선순위대로 체크할 키 목록
	 * @return 선택된 메타태그 값
	 */
	private String getMetaTagValueWithPriority(Map<String, String> metaTags, String... keys) {
		for (String key : keys) {
			if (metaTags.containsKey(key) && metaTags.get(key) != null && !metaTags.get(key).isEmpty()) {
				return metaTags.get(key);
			}
		}
		return null; // 적절한 값이 없을 경우 null 반환
	}

	/**
	 * 메타태그에서 title, description, image 값을 가져오는 메서드.
	 *
	 * @param url 메타태그를 파싱할 URL
	 * @return title, description, image를 포함한 맵
	 * @throws IOException URL 연결 오류 발생 시 예외 처리
	 */
	public Map<String, String> getBookmarkMetaData(String url, Long userId) throws IOException {
		Map<String, String> metaTags = parseMetaTags(url);

		String title = getMetaTagValueWithPriority(metaTags, "og:title", "twitter:title", "title");
		String description = getMetaTagValueWithPriority(metaTags, "og:description", "twitter:description",
				"description");
		String image = getMetaTagValueWithPriority(metaTags, "og:image", "twitter:image");

		Map<String, String> bookmarkData = new HashMap<>();
		bookmarkData.put("title", title);
		bookmarkData.put("description", description);
		bookmarkData.put("image", image);

		// 이미지 메타 태그가 없는 경우 스크린샷을 찍음
		if (!metaTags.containsKey("og:image") && !metaTags.containsKey("twitter:image")) {
			String screenshotPath = takeScreenshot(bookmarkData, url, userId);
			bookmarkData.put("image", screenshotPath);
		}

		return bookmarkData;
	}

	public String takeScreenshot(Map<String, String> bookmarkData, String url, Long userId) throws IOException {
		String filename;
		String coverImageURL;

		WebDriverManager.chromedriver().setup();
		String driverVersion = WebDriverManager.chromedriver().getDriverVersions().toString();
		log.debug("driverVersion : " + driverVersion);
		

		ChromeOptions options = new ChromeOptions();
		options.addArguments(
			    "--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/" + driverVersion
			);
		options.addArguments("--headless"); // 브라우저를 숨김 모드로 실행

		WebDriver driver = new ChromeDriver(options);

		try {
			// 페이지 이동
			driver.get(url);

			// 스크린샷 찍기
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			// "title" 값이 null이 아닌지 확인하여 "Untitled"로 대체
			String title = Optional.ofNullable(bookmarkData.get("title")).orElse("Untitled");
			filename = userId + "/screenshots/" + title.replaceAll("[^a-zA-Z0-9-_\\.]", "_") + ".png";

			coverImageURL = imageUploadService.upload(screenshot, filename);

		} finally {
			driver.quit(); // 브라우저 종료
		}

		return coverImageURL;
	}
	
}
