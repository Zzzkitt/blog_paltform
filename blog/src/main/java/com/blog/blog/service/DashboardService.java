package com.blog.blog.service;

import com.blog.blog.enums.ArticleStatus;
import com.blog.blog.repository.ArticleRepository;
import com.blog.blog.repository.CategoryRepository;
import com.blog.blog.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalArticles", articleRepository.count());
        stats.put("publishedArticles", articleRepository.countByStatus(ArticleStatus.PUBLISHED));
        stats.put("draftArticles", articleRepository.countByStatus(ArticleStatus.DRAFT));
        stats.put("totalCategories", categoryRepository.count());
        stats.put("totalTags", tagRepository.count());
        return stats;
    }
}
