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
@Table(name = "bookmark")
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class BookmarkEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "bookmark_id")
	private Long bookmarkId;

	@Column(name = "title", nullable = false, length = 300)
	private String title;

	@Column(name = "url", nullable = false, length = 500)
	private String url;

	@Column(name = "cover_img_url", length = 500)
	private String coverImgUrl;

	@Column(name = "description", columnDefinition = "TEXT")
	private String note;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
	@ToString.Exclude
	@JsonIgnore
	private UserEntity user;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;
	
	@OneToMany(mappedBy = "bookmark", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<CollectionBookmark> collectionBookmarks = new HashSet<>();

	@Builder
    public BookmarkEntity(String title, String url, String coverImgUrl, String note, UserEntity user) {
        this.title = title;
        this.url = url;
        this.coverImgUrl = coverImgUrl;
        this.note = note;
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
