package com.nestify.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nestify.entity.CollectionEntity;

@Service
public interface NestService {
	List<CollectionEntity> getCollectionsByUserId(Long userId);				//사용자별 컬렉션 리스트
	CollectionEntity saveCollection(CollectionEntity collectionEntity);		//컬렉션 등록
	CollectionEntity updateCollection(CollectionEntity collectionEntity);	//컬렉션 수정
	void deleteCollectionById(Long collectionId);					//컬렉션 삭제
}
