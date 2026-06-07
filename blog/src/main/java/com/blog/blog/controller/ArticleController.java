package com.blog.blog.controller;

import com.blog.blog.common.Result;
import com.blog.blog.dto.ArticleCreateRequest;
import com.blog.blog.dto.ArticleUpdateRequest;
import com.blog.blog.service.ArticleService;
import com.blog.blog.vo.ArticleVO;
import com.blog.blog.vo.PageVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/articles")
@RequiredArgsConstructor
@Tag(name = "文章管理", description = "管理端文章 CRUD 接口")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    @Operation(summary = "获取文章列表（分页）")
    public Result<PageVO<ArticleVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return Result.success(new PageVO<>(articleService.findAll(pageRequest)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取文章详情")
    public Result<ArticleVO> getById(@PathVariable Long id) {
        return Result.success(articleService.findById(id));
    }

    @PostMapping
    @Operation(summary = "新建文章")
    public Result<ArticleVO> create(@Valid @RequestBody ArticleCreateRequest request) {
        return Result.success("文章创建成功", articleService.createArticle(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑文章")
    public Result<ArticleVO> update(@PathVariable Long id, @Valid @RequestBody ArticleUpdateRequest request) {
        return Result.success("文章更新成功", articleService.updateArticle(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.success("文章删除成功", null);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "切换文章状态（发布/草稿）")
    public Result<ArticleVO> toggleStatus(@PathVariable Long id) {
        return Result.success(articleService.toggleStatus(id));
    }
}
