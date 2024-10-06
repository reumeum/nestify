package com.nestify.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookmarkForm {
	private Long bookmarkId;
	private String title;
	private String url;
	private String note;
	private List<Long> collectionId;
	private MultipartFile coverImg;
}
