package com.blog.blog.controller;

import com.blog.blog.common.Result;
import com.blog.blog.service.ArticleService;
import com.blog.blog.vo.ArchiveEntry;
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
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Tag(name = "公开文章接口", description = "前台文章展示接口")
public class PublicArticleController {

    private final ArticleService articleService;

    @GetMapping
    @Operation(summary = "分页获取已发布文章列表")
    public Result<PageVO<ArticleVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return Result.success(new PageVO<>(articleService.findPublished(pageRequest)));
    }

    @GetMapping("/top")
    @Operation(summary = "获取置顶文章")
    public Result<List<ArticleVO>> top() {
        return Result.success(articleService.findTopArticles());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文章详情（公开，自动+1阅读数）")
    public Result<ArticleVO> detail(@PathVariable Long id) {
        return Result.success(articleService.findPublishedById(id));
    }

    @GetMapping("/archive")
    @Operation(summary = "获取文章归档（按年月分组）")
    public Result<List<ArchiveEntry>> archive() {
        return Result.success(articleService.findArchive());
    }
}
