package com.nestify.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectionForm {
	private Long collectionId;
	private String colorCode;
	private String name;
	private String description;
}
