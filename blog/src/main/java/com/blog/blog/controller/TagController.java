package com.blog.blog.controller;

import com.blog.blog.common.Result;
import com.blog.blog.entity.Tag;
import com.blog.blog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tags")
@RequiredArgsConstructor
@io.swagger.v3.oas.annotations.tags.Tag(name = "标签管理", description = "管理端标签 CRUD 接口")
public class TagController {

    private final TagService tagService;

    @GetMapping
    @Operation(summary = "获取全部标签")
    public Result<List<Tag>> list() {
        return Result.success(tagService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取标签详情")
    public Result<Tag> getById(@PathVariable Long id) {
        return Result.success(tagService.findById(id));
    }

    @PostMapping
    @Operation(summary = "新增标签")
    public Result<Tag> create(@RequestBody Tag tag) {
        return Result.success("标签创建成功", tagService.createTag(tag));
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑标签")
    public Result<Tag> update(@PathVariable Long id, @RequestBody Tag tag) {
        return Result.success("标签更新成功", tagService.updateTag(id, tag));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除标签")
    public Result<Void> delete(@PathVariable Long id) {
        tagService.deleteTag(id);
        return Result.success("标签删除成功", null);
    }
}
