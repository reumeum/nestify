package com.nestify.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.nestify.dto.BookmarkDTO;
import com.nestify.dto.BookmarkForm;
import com.nestify.dto.CollectionForm;
import com.nestify.entity.BookmarkEntity;
import com.nestify.entity.CollectionEntity;
import com.nestify.entity.UserEntity;

@Service
public interface NestService {
	List<CollectionEntity> getCollectionsByIds(List<Long> collectionIds);	//컬렉션 id로 컬렉션 배열 불러오기
	Optional<CollectionEntity> getCollectionById(Long collectionId);		//컬렉션 id로 개별 컬렉션 불러오기
	List<CollectionEntity> getCollectionsByUserId(Long userId);				//사용자별 컬렉션 리스트
	CollectionEntity saveCollection(CollectionEntity collectionEntity);		//컬렉션 등록
	CollectionEntity updateCollection(CollectionForm collectionForm, UserEntity user);	//컬렉션 수정
	void deleteCollectionById(Long collectionId);							//컬렉션 삭제
    
    Page<BookmarkDTO> searchBookmarks(Long userId, Long collectionId, String keyword, int page, int size, String sortBy, boolean desc);
    
    BookmarkDTO getBookmarkByBookmarkId(Long bookmarkId);				//북마크 조회
	BookmarkEntity saveBookmark(BookmarkEntity bookmark, Long collectionId, Long userId);				//북마크 등록
	BookmarkEntity updateBookmark(BookmarkForm bookmarkForm, UserEntity user);			//북마크 수정
	void deleteBookmarkById(Long bookmarkId);								//북마크 삭제
}
