package com.blog.blog.service;

import com.blog.blog.entity.Category;
import com.blog.blog.repository.ArticleRepository;
import com.blog.blog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;

    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, Category category) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        existing.setName(category.getName());
        existing.setSlug(category.getSlug());
        existing.setDescription(category.getDescription());
        existing.setSortOrder(category.getSortOrder());
        return categoryRepository.save(existing);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        // 检查是否有文章引用该分类
        if (articleRepository.existsByCategory(category)) {
            throw new RuntimeException("该分类下存在文章，无法删除");
        }
        categoryRepository.delete(category);
    }

    public List<Category> findAll() {
        return categoryRepository.findAllByOrderBySortOrderAsc();
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
    }
}