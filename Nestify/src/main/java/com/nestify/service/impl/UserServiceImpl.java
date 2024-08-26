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
	public void deleteUserByUserId(Long userId) {
		userJpaRepository.deleteById(userId);
	}

	/*
	 * 사용자 인증
	 */
	@Override
    public boolean authenticate(String email, String password) {
        // 데이터베이스에서 사용자 조회
        UserEntity user = userJpaRepository.findByEmail(email);

        if (user != null) {
            return user.getPassword().equals(password);
        }

        // 사용자를 찾을 수 없거나 비밀번호가 일치하지 않는 경우 false 반환
        return false;
    }

	@Override
	public UserEntity findByEmail(String email) {
		return userJpaRepository.findByEmail(email);
	}
	
	
	
}
