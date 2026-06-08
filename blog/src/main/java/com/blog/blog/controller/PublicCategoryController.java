package com.blog.blog.controller;

import com.blog.blog.common.Result;
import com.blog.blog.entity.Category;
import com.blog.blog.service.ArticleService;
import com.blog.blog.service.CategoryService;
import com.blog.blog.vo.ArticleVO;
import com.blog.blog.vo.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "公开分类接口", description = "前台分类展示接口")
public class PublicCategoryController {

    private final CategoryService categoryService;
    private final ArticleService articleService;

    @GetMapping
    @Operation(summary = "获取全部分类")
    public Result<List<Category>> list() {
        return Result.success(categoryService.findAll());
    }

    @GetMapping("/{slug}/articles")
    @Operation(summary = "按分类 slug 获取文章")
    public Result<PageVO<ArticleVO>> articles(
            @PathVariable String slug,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "publishedAt"));
        return Result.success(new PageVO<>(articleService.findByCategorySlug(slug, pageRequest)));
    }
}
