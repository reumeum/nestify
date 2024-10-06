package com.nestify.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nestify.entity.UserEntity;
import com.nestify.service.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class UserRestController {
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
	@PostMapping("/api/v1/user")
	public ResponseEntity<Object> userSave(@RequestBody UserEntity userEntity) {
		UserEntity existingUser = userService.findByEmail(userEntity.getEmail());
		
		if (existingUser != null) {
	        Map<String, String> response = new HashMap<>();
	        response.put("message", "User with this email already exists");
	        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		} else {
			UserEntity result = userService.saveUser(userEntity);			
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		
	}

	/*
	 * 사용자 수정
	 */

	/*
	 * 사용자 삭제
	 */
	@DeleteMapping("/api/v1/user")
	public ResponseEntity<Object> deleteUser(@RequestParam("userId") Long userId) {
		userService.deleteUserByUserId(userId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

}
