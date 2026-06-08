package com.blog.blog.controller;

import com.blog.blog.common.Result;
import com.blog.blog.entity.Tag;
import com.blog.blog.service.ArticleService;
import com.blog.blog.service.TagService;
import com.blog.blog.vo.ArticleVO;
import com.blog.blog.vo.PageVO;
import io.swagger.v3.oas.annotations.Operation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "公开标签接口", description = "前台标签展示接口")
public class PublicTagController {

    private final TagService tagService;
    private final ArticleService articleService;

    @GetMapping
    @Operation(summary = "获取全部标签")
    public Result<List<Tag>> list() {
        return Result.success(tagService.findAll());
    }

    @GetMapping("/{slug}/articles")
    @Operation(summary = "按标签 slug 获取文章")
    public Result<PageVO<ArticleVO>> articles(
            @PathVariable String slug,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "publishedAt"));
        return Result.success(new PageVO<>(articleService.findByTagSlug(slug, pageRequest)));
    }
}
