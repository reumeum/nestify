package com.nestify.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nestify.entity.CollectionEntity;

@Repository
public interface CollectionJpaRepository extends JpaRepository<CollectionEntity, Long> {
	List<CollectionEntity> findByUser_UserIdAndIsSystemCollectionFalse(Long userId);
	
	Optional<CollectionEntity> findByUser_UserIdAndIsSystemCollectionTrue(Long userId);
}
