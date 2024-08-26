package com.nestify.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nestify.entity.CollectionEntity;
import com.nestify.entity.UserEntity;
import com.nestify.service.NestService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@RestController
public class NestRestController {
	
	private final NestService nestService;
	
    /*
     * 특정 사용자의 컬렉션 목록 조회
     */
    @GetMapping("/api/v1/users/{userId}/collections")
    public ResponseEntity<Object> getCollectionsByUserId(@PathVariable("userId") Long userId) {
    	log.debug("userId : " + userId);
    	
        List<CollectionEntity> collections = nestService.getCollectionsByUserId(userId);
        return new ResponseEntity<>(collections, HttpStatus.OK);
    }

    /*
     * 컬렉션 등록
     */
    @PostMapping("/api/v1/collection")
    public ResponseEntity<Object> saveCollection(@RequestBody CollectionEntity collectionEntity, HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        if (user != null) {
            collectionEntity.setUser(user); // UserEntity 객체 설정
        } else {
            return new ResponseEntity<>("User not found in session", HttpStatus.UNAUTHORIZED);
        }
        
        CollectionEntity result = nestService.saveCollection(collectionEntity);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    
	/*
	 * 컬렉션 수정
	 */

    /*
     * 컬렉션 삭제
     */
    @DeleteMapping("/api/v1/collection")
    public ResponseEntity<Object> deleteCollection(@RequestParam("collectionId") Long collectionId) {
        nestService.deleteCollectionById(collectionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}