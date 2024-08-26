package com.nestify.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nestify.entity.CollectionEntity;
import com.nestify.repository.CollectionJpaRepository;
import com.nestify.service.NestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@RequiredArgsConstructor
public class NestServiceImpl implements NestService {

	private final CollectionJpaRepository collectionJpaRepository;
	
	/*
	 * 사용자별 컬렉션 리스트 조회
	 */
	@Override
	public List<CollectionEntity> getCollectionsByUserId(Long userId) {
	    System.out.println("Fetching collections for userId: " + userId);
	    List<CollectionEntity> collections = collectionJpaRepository.findByUserUserId(userId);
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

}
