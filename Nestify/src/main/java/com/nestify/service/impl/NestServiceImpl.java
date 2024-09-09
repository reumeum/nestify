package com.nestify.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nestify.entity.BookmarkEntity;
import com.nestify.entity.CollectionEntity;
import com.nestify.repository.BookmarkJpaRepository;
import com.nestify.repository.CollectionJpaRepository;
import com.nestify.repository.UserJpaRepository;
import com.nestify.service.NestService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class NestServiceImpl implements NestService {

	private final UserJpaRepository userJpaRepository;
	private final CollectionJpaRepository collectionJpaRepository;
	private final BookmarkJpaRepository bookmarkJpaRepository;

	/*
	 * 사용자별 컬렉션 리스트 조회
	 */
	@Override
	public List<CollectionEntity> getCollectionsByUserId(Long userId) {
		System.out.println("Fetching collections for userId: " + userId);
		List<CollectionEntity> collections = collectionJpaRepository
				.findByUser_UserIdAndIsSystemCollectionFalse(userId);
		log.debug("Collections found: " + collections.size());
		for (CollectionEntity collection : collections) {
			log.debug(collection.getName());
		}
		return collections;
	}

	/*
	 * 컬렉션 등록
	 */
	@Override
	public CollectionEntity saveCollection(CollectionEntity collectionEntity) {
		return collectionJpaRepository.save(collectionEntity);
	}

	/*
	 * 컬렉션 수정
	 */
	@Override
	public CollectionEntity updateCollection(CollectionEntity collectionEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * 컬렉션 삭제
	 */
	@Override
	public void deleteCollectionById(Long collectionId) {
		collectionJpaRepository.deleteById(collectionId);
	}

	/*
	 * 모든 북마크 리스트
	 */
	@Override
	public List<BookmarkEntity> getBookmarksByUserId(Long userId) {
		return bookmarkJpaRepository.findByUser_UserId(userId);
	}

	/*
	 * 컬렉션별 북마크 리스트
	 */
	@Override
	public List<BookmarkEntity> getBookmarksByCollectionId(Long collectionId) {
		return bookmarkJpaRepository.findByCollections_CollectionId(collectionId);
	}

	/*
	 * favorite 북마크 리스트
	 */
	@Override
	public List<BookmarkEntity> getFavoriteBookmarksByUserId(Long userId) {
		return bookmarkJpaRepository.findByUser_UserIdAndIsFavoriteTrue(userId);
	}

	@Override
	public BookmarkEntity saveBookmark(BookmarkEntity bookmark, Long collectionId, Long userId) {
		CollectionEntity collection;

		if (collectionId == null) {
			// 컬렉션 ID가 지정되지 않은 경우 해당 사용자의 기본 Unsorted 컬렉션을 사용
			collection = collectionJpaRepository.findByUser_UserIdAndIsSystemCollectionTrue(userId)
					.orElseGet(() -> createUnsortedCollectionForUser(userId));
		} else {
			collection = collectionJpaRepository.findById(collectionId)
					.orElseThrow(() -> new RuntimeException("Collection not found"));
		}

		collection.getBookmarks().add(bookmark);

		bookmarkJpaRepository.save(bookmark);
		collectionJpaRepository.save(collection);

		return bookmark;
	}

	private CollectionEntity createUnsortedCollectionForUser(Long userId) {
		CollectionEntity unsorted = new CollectionEntity();
		unsorted.setName("Unsorted");
		unsorted.setSystemCollection(true);
		unsorted.setUser(userJpaRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
		return collectionJpaRepository.save(unsorted);
	}

	@Override
	public BookmarkEntity updateBookmark(BookmarkEntity bookmarkEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteBookmarkById(Long bookmarkId) {
		Optional<BookmarkEntity> optionalBookmark = bookmarkJpaRepository.findById(bookmarkId);

		if (optionalBookmark.isPresent()) {
			BookmarkEntity bookmark = optionalBookmark.get();
			for (CollectionEntity collection : bookmark.getCollections()) {
				collection.getBookmarks().remove(bookmark);
				collectionJpaRepository.save(collection);
			}

			bookmarkJpaRepository.deleteById(bookmarkId);
		}
	}
}
