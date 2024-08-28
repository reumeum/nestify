package com.nestify.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nestify.entity.BookmarkEntity;
import com.nestify.entity.CollectionEntity;

@Service
public interface NestService {
	List<CollectionEntity> getCollectionsByUserId(Long userId);				//사용자별 컬렉션 리스트
	CollectionEntity saveCollection(CollectionEntity collectionEntity);		//컬렉션 등록
	CollectionEntity updateCollection(CollectionEntity collectionEntity);	//컬렉션 수정
	void deleteCollectionById(Long collectionId);							//컬렉션 삭제
	
	List<BookmarkEntity> getBookmarksByUserId(Long userId);					//사용자별 북마크 리스트
    List<BookmarkEntity> getBookmarksByCollectionId(Long collectionId);     // 특정 컬렉션 ID로 북마크 불러오기
    List<BookmarkEntity> getFavoriteBookmarksByUserId(Long userId);         // 사용자별 즐겨찾기 북마크만 불러오기
    
	BookmarkEntity saveBookmark(BookmarkEntity bookmark, Long collectionId, Long userId);				//북마크 등록
	BookmarkEntity updateBookmark(BookmarkEntity bookmarkEntity);			//북마크 수정
	void deleteBookmarkById(Long bookmarkId);								//북마크 삭제
}
