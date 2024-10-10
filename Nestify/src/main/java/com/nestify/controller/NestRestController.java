package com.nestify.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nestify.dto.BookmarkDTO;
import com.nestify.dto.BookmarkForm;
import com.nestify.dto.CollectionForm;
import com.nestify.entity.BookmarkEntity;
import com.nestify.entity.CollectionEntity;
import com.nestify.entity.UserEntity;
import com.nestify.service.NestService;
import com.nestify.util.MetaTagParser;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@RestController
public class NestRestController {

	private final NestService nestService;
	private final MetaTagParser metaTagParser;

	/*
	 * 특정 사용자의 컬렉션 목록 조회
	 */
	@GetMapping("/api/v1/collections/{userId}")
	public ResponseEntity<Object> getCollectionsByUserId(@PathVariable("userId") Long userId) {
		log.debug("userId : " + userId);

		List<CollectionEntity> collections = nestService.getCollectionsByUserId(userId);
		return new ResponseEntity<>(collections, HttpStatus.OK);
	}

	/*
	 * 컬렉션 등록
	 */
	@PostMapping("/api/v1/collection")
	public ResponseEntity<Object> saveCollection(@RequestBody CollectionEntity collectionEntity, HttpSession session) {
		UserEntity user = (UserEntity) session.getAttribute("user");

		collectionEntity.setUser(user); // UserEntity 객체 설정

		CollectionEntity result = nestService.saveCollection(collectionEntity);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/*
	 * 컬렉션 수정
	 */
	@PutMapping("/api/v1/collection/{collectionId}")
	public ResponseEntity<Object> updateCollection(@PathVariable("collectionId") Long collectionId,
	        @ModelAttribute CollectionForm collectionForm, HttpSession session) {

	    UserEntity user = (UserEntity) session.getAttribute("user");
	    try {
	        // Service 계층으로 비즈니스 로직 위임
	    	collectionForm.setCollectionId(collectionId);
	    	nestService.updateCollection(collectionForm, user);
	    } catch (RuntimeException e) {
	        log.error("Error updating bookmark", e);
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	    }

	    return new ResponseEntity<>(HttpStatus.OK);
	}	


	/*
	 * 컬렉션 삭제
	 */
	@DeleteMapping("/api/v1/collection/{collectionId}")
	public ResponseEntity<Object> deleteCollection(@PathVariable("collectionId") Long collectionId) {
		nestService.deleteCollectionById(collectionId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/*
	 * 북마크 등록
	 */
	// "/api/v1/collection/bookmark" --> add to unsorted
	@PostMapping({ "/api/v1/collection/{collectionId}/bookmark", "/api/v1/bookmark" })
	public ResponseEntity<Object> saveBookmark(
			@PathVariable(value = "collectionId", required = false) Long collectionId,
			@RequestBody BookmarkEntity bookmarkEntity, HttpSession session) {

		UserEntity user = (UserEntity) session.getAttribute("user");

		log.debug("collectionId : " + collectionId);
		log.debug("bookmark URL : " + bookmarkEntity.getUrl());

		bookmarkEntity.setUser(user); // UserEntity 객체 설정
		Map<String, String> bookmarkData = null;
		try {
			bookmarkData = metaTagParser.getBookmarkMetaData(bookmarkEntity.getUrl(), user.getUserId());
		} catch (IOException e) {
			log.error("Failed to parse meta tags for URL: " + bookmarkEntity.getUrl(), e);
			return new ResponseEntity<>("Failed to parse meta tags for the provided URL", HttpStatus.BAD_REQUEST);
		}

		bookmarkEntity.setTitle(Optional.ofNullable(bookmarkData.get("title")).orElse("Untitled"));
		bookmarkEntity.setNote(bookmarkData.get("description"));
		bookmarkEntity.setCoverImgUrl(bookmarkData.get("image"));

		log.debug("title : " + bookmarkEntity.getTitle());
		log.debug("note : " + bookmarkEntity.getNote());
		log.debug("coverImgUrl : " + bookmarkEntity.getCoverImgUrl());

		BookmarkEntity result = nestService.saveBookmark(bookmarkEntity, collectionId, user.getUserId());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/*
	 * 북마크 조회
	 */
	@GetMapping("/api/v1/bookmark/{bookmarkId}")
	public ResponseEntity<Object> getBookmarkByBookmarkId(@PathVariable("bookmarkId") Long bookmarkId) {
		log.debug("bookmarkId : " + bookmarkId);

		BookmarkDTO bookmarkDTO = nestService.getBookmarkByBookmarkId(bookmarkId);
		
		log.debug("BookmarkDTO : " + bookmarkDTO);

		return new ResponseEntity<>(bookmarkDTO, HttpStatus.OK);
	}

	/*
	 * 북마크 삭제
	 */
	@DeleteMapping("/api/v1/bookmark/{bookmarkId}")
	public ResponseEntity<Object> deleteBookmarkByBookmarkId(@PathVariable("bookmarkId") Long bookmarkId) {
		nestService.deleteBookmarkById(bookmarkId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/*
	 * 북마크 수정
	 */
	@PutMapping("/api/v1/bookmark/{bookmarkId}")
	public ResponseEntity<Object> updateBookmark(@PathVariable("bookmarkId") Long bookmarkId,
	        @ModelAttribute BookmarkForm bookmarkForm, HttpSession session) {

	    UserEntity user = (UserEntity) session.getAttribute("user");

	    try {
	        // Service 계층으로 비즈니스 로직 위임
	    	bookmarkForm.setBookmarkId(bookmarkId);
	        nestService.updateBookmark(bookmarkForm, user);
	    } catch (RuntimeException e) {
	        log.error("Error updating bookmark", e);
	        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
	    }

	    return new ResponseEntity<>(HttpStatus.OK);
	}	
	
	/*
	 * 북마크 검색
	 */
	@GetMapping({"/api/v1/bookmarks/{userId}/collection/{collectionId}", "/api/v1/bookmarks/{userId}"})
	public ResponseEntity<Object> searchBookmarks(HttpSession session,
												  @PathVariable("userId") Long userId,
												  @PathVariable(value = "collectionId", required = false) Long collectionId,												  
												  @RequestParam(value = "keyword", required = false) String keyword,
												  @RequestParam(value = "page", defaultValue = "0") int page,
												  @RequestParam(value = "size", defaultValue = "10") int size,
												  @RequestParam(value = "sortBy", defaultValue = "updatedAt") String sortBy,
												  @RequestParam(value = "desc", defaultValue = "true") boolean desc) {


		Page<BookmarkDTO> bookmarkDTOs = nestService.searchBookmarks(userId, collectionId, keyword, page, size, sortBy, desc);
		
		for (BookmarkDTO bookmark : bookmarkDTOs) {
			log.debug(bookmark);
		}
		
		return new ResponseEntity<>(bookmarkDTOs.getContent(), HttpStatus.OK);
	}
	
	
	

}