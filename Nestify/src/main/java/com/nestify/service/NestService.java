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

    // 컬렉션
    List<CollectionEntity> getCollectionsByIds(List<Long> collectionIds);  // 컬렉션 ID로 목록 조회
    Optional<CollectionEntity> getCollectionById(Long collectionId);       // 컬렉션 ID로 단일 조회
    List<CollectionEntity> getCollectionsByUserId(Long userId);            // 사용자 ID로 컬렉션 목록 조회
    CollectionEntity saveCollection(CollectionEntity collectionEntity);    // 컬렉션 저장
    CollectionEntity updateCollection(CollectionForm collectionForm, UserEntity user);  // 컬렉션 수정
    void deleteCollectionById(Long collectionId);                          // 컬렉션 삭제
    boolean isUserOwnerOfCollection(Long userId, Long resourceId);         // 사용자 소유 컬렉션인지 확인

    // 북마크
    Page<BookmarkDTO> searchBookmarks(Long userId, Long collectionId, String keyword, int page, int size, String sortBy, boolean desc);  // 북마크 검색
    BookmarkDTO getBookmarkByBookmarkId(Long bookmarkId);                  // 북마크 ID로 조회
    BookmarkEntity saveBookmark(BookmarkEntity bookmark, Long collectionId, Long userId);  // 북마크 저장
    BookmarkEntity updateBookmark(BookmarkForm bookmarkForm, UserEntity user);  // 북마크 수정
    void deleteBookmarkById(Long bookmarkId);                              // 북마크 삭제
    boolean isUserOwnerOfBookmark(Long userId, Long resourceId);           // 사용자 소유 북마크인지 확인
}
