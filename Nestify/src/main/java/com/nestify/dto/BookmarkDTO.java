package com.nestify.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nestify.entity.BookmarkEntity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class BookmarkDTO {
	private Long bookmarkId;
	private String title;
	private String url;
	private String coverImgUrl;
	private String note;
	private Long collectionId;
	private String collectionName;
	@JsonProperty("isSystemCollection")
    private boolean isSystemCollection;
	private String collectionColorCode;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	

    public BookmarkDTO(BookmarkEntity bookmark) {
        this.bookmarkId = bookmark.getBookmarkId();
        this.title = bookmark.getTitle();
        this.url = bookmark.getUrl();
        this.coverImgUrl = bookmark.getCoverImgUrl();
        this.note = bookmark.getNote();
        this.createdAt = bookmark.getCreatedAt();
        this.updatedAt = bookmark.getUpdatedAt();

        // collection 접근
        bookmark.getCollectionBookmarks().stream().findFirst().ifPresent(cb -> {
            this.collectionId = cb.getCollection().getCollectionId();
            this.collectionName = cb.getCollection().getName();
            this.isSystemCollection = cb.getCollection().isSystemCollection();
            this.collectionColorCode = cb.getCollection().getColorCode();
        });
	}
}
