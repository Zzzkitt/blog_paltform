package com.blog.blog.controller;

import com.blog.blog.common.Result;
import com.blog.blog.entity.Category;
import com.blog.blog.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
@Tag(name = "分类管理", description = "管理端分类 CRUD 接口")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "获取全部分类")
    public Result<List<Category>> list() {
        return Result.success(categoryService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取分类详情")
    public Result<Category> getById(@PathVariable Long id) {
        return Result.success(categoryService.findById(id));
    }

    @PostMapping
    @Operation(summary = "新增分类")
    public Result<Category> create(@RequestBody Category category) {
        return Result.success("分类创建成功", categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    @Operation(summary = "编辑分类")
    public Result<Category> update(@PathVariable Long id, @RequestBody Category category) {
        return Result.success("分类更新成功", categoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("分类删除成功", null);
    }
}
