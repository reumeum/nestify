package com.nestify.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.nestify.entity.UserEntity;

@Service
public interface UserService {

    List<UserEntity> userList();                    // 모든 사용자 목록 조회
    UserEntity saveUser(UserEntity userEntity);     // 새로운 사용자 저장
    UserEntity updateUser(UserEntity userEntity);   // 사용자 정보 업데이트
    void deleteUserByUserId(Long userId);           // 사용자 ID로 사용자 삭제
    boolean authenticate(String email, String password); // 이메일과 비밀번호로 사용자 인증
    UserEntity findByEmail(String email);           // 이메일로 사용자 조회

}
