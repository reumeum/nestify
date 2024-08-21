package com.nestify.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.nestify.entity.UserEntity;
import com.nestify.repository.UserJpaRepository;
import com.nestify.service.UserService;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	
	private final UserJpaRepository userJpaRepository;
	
	/*
	 * 사용자 리스트 조회
	 */
	@Override
	@Transactional(readOnly = true)
	public List<UserEntity> userList() {
		return (List<UserEntity>) userJpaRepository.findAll();
	}

	/*
	 * 사용자 등록
	 */
	@Override
	@Transactional
	public UserEntity saveUser(UserEntity userEntity) {
		return userJpaRepository.save(userEntity);
	}

	/*
	 * 사용자 수정
	 */
	@Override
	public UserEntity updateUser(UserEntity userEntity) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * 사용자 삭제
	 */
	@Override
	public void deleteUserByUserId(long userId) {
		userJpaRepository.deleteById(userId);
	}
	
}
