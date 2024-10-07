package com.nestify.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.nestify.entity.BookmarkEntity;
import com.nestify.entity.CollectionEntity;
import com.nestify.entity.UserEntity;
import com.nestify.service.NestService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RequiredArgsConstructor
@Controller
public class NestController {

	private final NestService nestService;

	/*dashboard*/
	@GetMapping({"/dashboard", "/dashboard/{collectionId}"})
	public String dashboard(
	    @PathVariable(value = "collectionId", required = false) Long collectionId, 
	    Model model, 
	    HttpSession session) {
	    
	    UserEntity user = (UserEntity) session.getAttribute("user");
	    CollectionEntity collection;
	    if (collectionId > 0) {
	    	collection = nestService.getCollectionById(collectionId)
	    			.orElseThrow(() -> new RuntimeException("Collection not found!"));
	    	model.addAttribute("collection", collection);
	    	log.debug("collection added to model" + collection);
	    }
	    
	    model.addAttribute("user", user);

	    return "nest/dashboard";
	}
	

	/*북마크 수정 페이지*/
	@GetMapping("/dashboard/{collectionId}/bookmark/{bookmarkId}/edit")
	public String editBookmark(@PathVariable("collectionId") Long collectionId,
			@PathVariable("bookmarkId") Long bookmarkId, Model model, HttpSession session) {

		UserEntity user = (UserEntity) session.getAttribute("user");
	    CollectionEntity collection;
	    if (collectionId > 0) {
	    	collection = nestService.getCollectionById(collectionId)
	    			.orElseThrow(() -> new RuntimeException("Collection not found!"));
	    	model.addAttribute("collection", collection);
	    	log.debug("collection added to model" + collection);
	    }

		
		// 컬렉션 리스트 조회
		List<CollectionEntity> collections = nestService.getCollectionsByUserId(user.getUserId());
				
		// 모델에 사용자, 컬렉션 정보 추가
		model.addAttribute("user", user);
		model.addAttribute("collections", collections);

		return "nest/dashboard"; // 대시보드 페이지로 이동

	}
}
