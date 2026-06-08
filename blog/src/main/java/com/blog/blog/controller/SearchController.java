package com.blog.blog.controller;

import com.blog.blog.common.Result;
import com.blog.blog.service.ArticleService;
import com.blog.blog.vo.ArticleVO;
import com.blog.blog.vo.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Tag(name = "搜索接口", description = "文章搜索接口")
public class SearchController {

    private final ArticleService articleService;

    @GetMapping("/search")
    @Operation(summary = "搜索文章（MySQL 全文检索）")
    public Result<PageVO<ArticleVO>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "publishedAt"));
        return Result.success(new PageVO<>(articleService.searchByKeyword(q, pageRequest)));
    }
}
