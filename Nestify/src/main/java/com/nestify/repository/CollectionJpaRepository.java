package com.nestify.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nestify.entity.CollectionEntity;

@Repository
public interface CollectionJpaRepository extends JpaRepository<CollectionEntity, Long> {
	List<CollectionEntity> findByUserUserId(Long userId);
}
