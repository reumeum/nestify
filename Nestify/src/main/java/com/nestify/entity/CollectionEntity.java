package com.nestify.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Entity
@Getter
@Setter
@Table(name="collection")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CollectionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "collection_id")
    private Long collectionId;
    
    @Column(name = "name", nullable = false, length = 180)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "color_code", nullable = false, length = 7)
    private String colorCode;
    
    // 연관된 UserEntity 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    @JsonIgnore
    private UserEntity user;
    
    // 기본값 설정
    private LocalDateTime created_at = LocalDateTime.now();
    private LocalDateTime updated_at = LocalDateTime.now();
    
    @Builder
    public CollectionEntity(String name, String description, String colorCode, UserEntity user, LocalDateTime created_at, LocalDateTime updated_at) {
        this.name = name;
        this.description = description;
        this.colorCode = colorCode;
        this.user = user;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}
