package com.blog.blog.vo;

import com.blog.blog.enums.ArticleStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class ArticleVO {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private String rawContent;
    private String coverImage;
    private ArticleStatus status;
    private Long categoryId;
    private String categoryName;
    private Set<Long> tagIds;
    private Set<String> tagNames;
    private Boolean isTop;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
}
