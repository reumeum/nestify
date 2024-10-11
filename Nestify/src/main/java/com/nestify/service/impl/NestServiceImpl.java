package com.nestify.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.nestify.dto.BookmarkDTO;
import com.nestify.dto.BookmarkForm;
import com.nestify.dto.CollectionForm;
import com.nestify.entity.BookmarkEntity;
import com.nestify.entity.CollectionBookmark;
import com.nestify.entity.CollectionEntity;
import com.nestify.entity.UserEntity;
import com.nestify.repository.BookmarkJpaRepository;
import com.nestify.repository.CollectionBookmarkJpaRepository;
import com.nestify.repository.CollectionJpaRepository;
import com.nestify.repository.UserJpaRepository;
import com.nestify.service.ImageUploadService;
import com.nestify.service.NestService;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
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
	private final CollectionBookmarkJpaRepository collectionBookmarkJpaRepository;
	private final ImageUploadService imageUploadService;

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
	@Transactional
	public CollectionEntity updateCollection(CollectionForm collectionForm, UserEntity user) {

		CollectionEntity collectionEntity = collectionJpaRepository.findById(collectionForm.getCollectionId())
				.orElseThrow(() -> new RuntimeException(
						"Collection not found with id: " + collectionForm.getCollectionId()));

		// 필드 업데이트
		collectionEntity.setCollectionId(collectionForm.getCollectionId());
		collectionEntity.setName(collectionForm.getName());
		collectionEntity.setColorCode(collectionForm.getColorCode());
		collectionEntity.setDescription(collectionForm.getDescription());
		collectionEntity.setUser(user);

		// updated_at 필드 갱신
		collectionEntity.setUpdatedAt(LocalDateTime.now());

		log.debug("컬렉션 수정 : " + collectionEntity);

		// 저장
		return collectionJpaRepository.save(collectionEntity);
	}

	/*
	 * 컬렉션 삭제
	 */
	@Override
	public void deleteCollectionById(Long collectionId) {
		collectionJpaRepository.deleteById(collectionId);
	}

	/*
	 * 북마크 등록
	 */
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

		CollectionBookmark collectionBookmark = new CollectionBookmark();
		collectionBookmark.setBookmark(bookmark);
		collectionBookmark.setCollection(collection);

		bookmarkJpaRepository.save(bookmark);
		collectionJpaRepository.save(collection);
		collectionBookmarkJpaRepository.save(collectionBookmark);

		return bookmark;
	}

	private CollectionEntity createUnsortedCollectionForUser(Long userId) {
		CollectionEntity unsorted = new CollectionEntity();
		unsorted.setName("Unsorted");
		unsorted.setSystemCollection(true);
		unsorted.setUser(userJpaRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")));
		return collectionJpaRepository.save(unsorted);
	}

	/*
	 * 북마크 수정
	 */
	@Override
	@Transactional
	public BookmarkEntity updateBookmark(BookmarkForm bookmarkForm, UserEntity user) {
		BookmarkEntity bookmarkEntity = bookmarkJpaRepository.findById(bookmarkForm.getBookmarkId())
				.orElseThrow(() -> new RuntimeException("Bookmark not found with id: " + bookmarkForm.getBookmarkId()));

		// 필드 업데이트
		bookmarkEntity.setTitle(bookmarkForm.getTitle());
		bookmarkEntity.setUrl(bookmarkForm.getUrl());
		bookmarkEntity.setNote(bookmarkForm.getNote());
		bookmarkEntity.setUser(user);

		// 기존 컬렉션을 명시적으로 제거
		for (CollectionBookmark collectionBookmark : bookmarkEntity.getCollectionBookmarks()) {
			collectionBookmarkJpaRepository.delete(collectionBookmark); // CollectionBookmark 삭제
		}

		bookmarkEntity.getCollectionBookmarks().clear(); // 관계를 제거

		// 컬렉션 업데이트
		List<Long> collectionIds = bookmarkForm.getCollectionId();
		List<CollectionEntity> collections = new ArrayList<CollectionEntity>();

		log.debug("collection Id (Form) : " + bookmarkForm.getCollectionId());

		if (bookmarkForm.getCollectionId().stream().findFirst().orElse((long) 0) == 0) {
			CollectionEntity unsortedCollection = collectionJpaRepository
					.findByUser_UserIdAndIsSystemCollectionTrue(user.getUserId())
					.orElseThrow(() -> new RuntimeException("Collection not found!"));
			collections.add(unsortedCollection);
		} else {
			collections = getCollectionsByIds(collectionIds);
		}

		Set<CollectionBookmark> collectionBookmarks = new HashSet<CollectionBookmark>();
		for (CollectionEntity collection : collections) {
			CollectionBookmark collectionBookmark = new CollectionBookmark();
			collectionBookmark.setBookmark(bookmarkEntity);
			collectionBookmark.setCollection(collection);
			collectionBookmarks.add(collectionBookmark);

			// 새로 추가된 CollectionBookmark 엔티티 저장
			collectionBookmarkJpaRepository.save(collectionBookmark);

			log.debug("북마크 수정 컬렉션 목록 : " + collection);
		}

		// 커버 이미지 처리
		MultipartFile coverImg = bookmarkForm.getCoverImg();

		if (coverImg != null && !coverImg.isEmpty()) {
			try {
				String fileName = user.getUserId() + "/coverImgs/" + coverImg.getOriginalFilename();
				bookmarkEntity.setCoverImgUrl(imageUploadService.upload(coverImg, fileName));
			} catch (IOException e) {
				throw new RuntimeException("Failed to save cover image", e);
			}
		}

		// updated_at 필드 갱신
		bookmarkEntity.setUpdatedAt(LocalDateTime.now());

		log.debug("북마크 수정 : " + bookmarkEntity);

		// 저장
		return bookmarkJpaRepository.save(bookmarkEntity);
	}

	@Override
	public List<CollectionEntity> getCollectionsByIds(List<Long> collectionIds) {
		return collectionJpaRepository.findAllById(collectionIds);
	}

	@Override
	public Optional<CollectionEntity> getCollectionById(Long collectionId) {
		return collectionJpaRepository.findById(collectionId);
	}

	@Override
	public void deleteBookmarkById(Long bookmarkId) {
		BookmarkEntity bookmark = bookmarkJpaRepository.findById(bookmarkId)
				.orElseThrow(() -> new RuntimeException("Bookmark not found!"));

		// 커버이미지가 있으면 삭제
		String coverImagePath = bookmark.getCoverImgUrl();

		if (coverImagePath != null
				&& coverImagePath.startsWith("https://nestifyimagebucket.s3.ap-northeast-2.amazonaws.com")) {
			try {
				String objectKey = getSubstringAfterThirdSlash(coverImagePath);
				imageUploadService.delete(objectKey);
				log.debug("Image deleted from the aws bucket. [objectKey] : " + objectKey);
			} catch (IOException e) {
				log.error("Failed to delete cover Image: " + coverImagePath, e);
			}
		}

		// 북마크 삭제
		bookmarkJpaRepository.delete(bookmark);
	}
	
	public String getSubstringAfterThirdSlash(String url) {
	    String[] parts = url.split("/");

	    // URL에서 세 번째 슬래시 이후의 부분을 결합
	    if (parts.length > 3) {
	        return String.join("/", Arrays.copyOfRange(parts, 3, parts.length));
	    }

	    return null; // 세 번째 슬래시가 없는 경우 처리
	}

	@Override
	public BookmarkDTO getBookmarkByBookmarkId(Long bookmarkId) {

		BookmarkEntity bookmark = bookmarkJpaRepository.findById(bookmarkId)
				.orElseThrow(() -> new RuntimeException("Bookmark not found"));

		BookmarkDTO bookmarkDTO = new BookmarkDTO(bookmark);

		return bookmarkDTO;
	}

	@Override
	public Page<BookmarkDTO> searchBookmarks(Long userId, Long collectionId, String keyword, int page, int size,
			String sortBy, boolean desc) {
		Pageable pageable = PageRequest.of(page, size,
				Sort.by(desc ? Sort.Direction.DESC : Sort.Direction.ASC, sortBy));

		// 동적 검색을 위한 Specification 생성
		Specification<BookmarkEntity> spec = (root, query, criteriaBuilder) -> {
			// 조인을 통해 컬렉션 접근 (bookmark와 collectionBookmarks 사이에 JOIN을 수행)
			Join<Object, Object> collectionBookmarksJoin = root.join("collectionBookmarks");

			List<Predicate> predicates = new ArrayList<>();

			// userId가 있을 때 사용자 필터링
			if (userId != null) {
				predicates.add(criteriaBuilder.equal(root.get("user").get("userId"), userId));
			}

			// collectionId가 있을 때 컬렉션 필터링
			if (collectionId != null && collectionId == -1) {
				predicates.add(criteriaBuilder
						.equal(collectionBookmarksJoin.get("collection").get("isSystemCollection"), true));
			} else if (collectionId != null) {
				predicates.add(criteriaBuilder.equal(collectionBookmarksJoin.get("collection").get("collectionId"),
						collectionId));
			}

			// 검색어가 있을 때 제목에서 대소문자 구분 없이 검색
			if (keyword != null && !keyword.isEmpty()) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),
						"%" + keyword.toLowerCase() + "%"));
				log.debug("키워드 : " + keyword);
			}

			// 모든 조건을 AND로 결합
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};

		// 조건에 맞는 결과를 페이징 처리하여 반환
		return bookmarkJpaRepository.findAll(spec, pageable).map(bookmarkEntity -> convertToDTO(bookmarkEntity));
	}

	public BookmarkDTO convertToDTO(BookmarkEntity bookmark) {
		// DTO로 변환
		BookmarkDTO dto = new BookmarkDTO();
		dto.setBookmarkId(bookmark.getBookmarkId());
		dto.setTitle(bookmark.getTitle());
		dto.setUrl(bookmark.getUrl());
		dto.setCoverImgUrl(bookmark.getCoverImgUrl());
		dto.setNote(bookmark.getNote());
		dto.setCreatedAt(bookmark.getCreatedAt());
		dto.setUpdatedAt(bookmark.getUpdatedAt());

		// Collection 정보가 있는 경우, 첫 번째 Collection 정보를 DTO에 설정
		bookmark.getCollectionBookmarks().stream().findFirst().ifPresent(cb -> {
			dto.setCollectionId(cb.getCollection().getCollectionId());
			dto.setCollectionName(cb.getCollection().getName());
			dto.setSystemCollection(cb.getCollection().isSystemCollection());
			dto.setCollectionColorCode(cb.getCollection().getColorCode());
		});

		return dto;
	}

	@Override
	public boolean isUserOwnerOfCollection(Long userId, Long resourceId) {
		return collectionJpaRepository.findById(resourceId)
				.map(collection -> collection.getUser().getUserId().equals(userId)) // Optional을 활용해 바로 비교
				.orElse(false); // 없으면 false 반환
	}

	@Override
	public boolean isUserOwnerOfBookmark(Long userId, Long resourceId) {
		return bookmarkJpaRepository.findById(resourceId).map(bookmark -> bookmark.getUser().getUserId().equals(userId)) // Optional을 활용해 바로 비교
				.orElse(false); // 없으면 false 반환
	}

}
