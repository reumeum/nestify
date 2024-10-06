package com.nestify.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
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
@NoArgsConstructor
public class CollectionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "collection_id")
    private Long collectionId;
    
    @Column(name = "name", nullable = false, length = 180)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "color_code", length = 7)
    private String colorCode;
    
    // 연관된 UserEntity 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    @JsonIgnore
    private UserEntity user;
    
    // 기본값 설정
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "is_system_collection", nullable = false)
    private boolean isSystemCollection = false;
    
    @OneToMany(mappedBy = "collection", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    @JsonIgnore
    private Set<CollectionBookmark> collectionBookmarks = new HashSet<>();
    
    @Builder
    public CollectionEntity(String name, String description, String colorCode, UserEntity user, LocalDateTime created_at, LocalDateTime updated_at) {
        this.name = name;
        this.description = description;
        this.colorCode = colorCode;
        this.user = user;
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
