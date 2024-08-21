package com.nestify.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	    @UniqueConstraint(columnNames = {"username"}),
	    @UniqueConstraint(columnNames = {"email"})
	})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEntity implements Serializable {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id")
	private long userId;
	
	@Column(name = "username", nullable = false, length = 50)
	private String username;
	
	@Column(name = "password", nullable = false, length = 100)
	private String password;
	
	@Column(name = "email", nullable = false, length = 100)
	private String email;
	
    // 기본값 설정
    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at = LocalDateTime.now();
	
	@Builder(toBuilder = true)
	public UserEntity(long userId, String username, String password, String email, LocalDateTime created_at, LocalDateTime updated_at) {
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.email = email;
		this.created_at = created_at;
		this.updated_at = updated_at;
	}
}
