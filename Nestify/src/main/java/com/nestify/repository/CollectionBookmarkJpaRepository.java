package com.nestify.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nestify.entity.CollectionBookmark;

@Repository
public interface CollectionBookmarkJpaRepository extends JpaRepository<CollectionBookmark, Long> {

}
