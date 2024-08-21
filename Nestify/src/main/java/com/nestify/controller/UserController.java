package com.nestify.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nestify.entity.UserEntity;
import com.nestify.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserController {
	private final UserService userService;
	
	/*
	 * 사용자 목록
	 */
	@GetMapping("/api/v1/users")
    public ResponseEntity<Object> selectUserList() {
        List<UserEntity> userEntityList = userService.userList();
        return new ResponseEntity<>(userEntityList, HttpStatus.OK);
    }
	
	/*
	 * 사용자 등록
	 */
	
	/*
	 * 사용자 수정
	 */
	
	/*
	 * 사용자 삭제
	 */
	
}
