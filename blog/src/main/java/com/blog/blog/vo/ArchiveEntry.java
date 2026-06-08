package com.blog.blog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ArchiveEntry {
    private String date;
    private List<ArticleVO> articles;
}
