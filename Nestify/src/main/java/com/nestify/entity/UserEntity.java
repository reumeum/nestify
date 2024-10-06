package com.nestify.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Entity
@Getter
@Table(name = "\"user\"", uniqueConstraints = {
	    @UniqueConstraint(columnNames = {"email"})
	})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id")
	private Long userId;
	
	@Column(name = "username", nullable = false, length = 50)
	private String username;
	
	@Column(name = "password", nullable = false, length = 100)
	private String password;
	
	@Column(name = "email", nullable = false, length = 100)
	private String email;
	
    // 기본값 설정
	@Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
	
	@Builder(toBuilder = true)
	public UserEntity(Long userId, String username, String password, String email) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.email = email;
	}
	
	//생성 시점에 설정
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}
	
	//갱신 시점에 설정
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
