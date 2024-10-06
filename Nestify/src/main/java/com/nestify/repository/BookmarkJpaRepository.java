package com.nestify.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.nestify.entity.BookmarkEntity;

@Repository
public interface BookmarkJpaRepository extends JpaRepository<BookmarkEntity, Long>, JpaSpecificationExecutor<BookmarkEntity> {

    List<BookmarkEntity> findByUser_UserId(Long userId);
}
