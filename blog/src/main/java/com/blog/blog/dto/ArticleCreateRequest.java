package com.blog.blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.Set;

@Data
public class ArticleCreateRequest {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String summary;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String coverImage;

    private String status;

    private Long categoryId;

    private Set<Long> tagIds;

    private Boolean isTop = false;
}
