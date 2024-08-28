package com.nestify.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
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
@Table(name = "bookmark")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	@Column(name = "is_favorite", nullable = false)
	private boolean isFavorite = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
	@JsonIgnore
	private UserEntity user;

	private LocalDateTime created_at = LocalDateTime.now();
	private LocalDateTime updated_at = LocalDateTime.now();
	
    @ManyToMany(mappedBy = "bookmarks")
    @JsonIgnore
    private Set<CollectionEntity> collections = new HashSet<>();

	@Builder
    public BookmarkEntity(String title, String url, String coverImgUrl, String note, boolean isFavorite, UserEntity user) {
        this.title = title;
        this.url = url;
        this.coverImgUrl = coverImgUrl;
        this.note = note;
        this.isFavorite = isFavorite;
        this.user = user;
        this.created_at = LocalDateTime.now();
        this.updated_at = LocalDateTime.now();
    }

}
