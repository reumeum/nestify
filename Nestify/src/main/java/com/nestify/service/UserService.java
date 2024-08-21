package com.nestify.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.nestify.entity.UserEntity;

@Service
public interface UserService {
	List<UserEntity> userList();					//사용자 리스트 조회
	UserEntity saveUser(UserEntity userEntity);		//사용자 등록
	UserEntity updateUser(UserEntity userEntity);	//사용자 수정
	void deleteUserByUserId(long userId);			//사용자 삭제
	boolean authenticate(String email, String password); //사용자 인증
	Optional<UserEntity> findByEmail(String email);
}
