package com.nestify.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.nestify.entity.BookmarkEntity;

@Repository
public interface BookmarkJpaRepository extends JpaRepository<BookmarkEntity, Long> {

    List<BookmarkEntity> findByUser_UserId(Long userId);
    
    List<BookmarkEntity> findByCollections_CollectionId(Long collectionId);
    
    List<BookmarkEntity> findByUser_UserIdAndIsFavoriteTrue(Long userId);
}
