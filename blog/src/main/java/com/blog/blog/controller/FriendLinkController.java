package com.blog.blog.controller;

import com.blog.blog.common.Result;
import com.blog.blog.entity.FriendLink;
import com.blog.blog.service.FriendLinkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "友链接口")
public class FriendLinkController {

    private final FriendLinkService friendLinkService;

    @GetMapping("/api/friend-links")
    @Operation(summary = "获取可见友链（公开）")
    public Result<List<FriendLink>> list() {
        return Result.success(friendLinkService.findVisible());
    }

    @GetMapping("/api/admin/friend-links")
    @Operation(summary = "获取全部友链（管理端）")
    public Result<List<FriendLink>> adminList() {
        return Result.success(friendLinkService.findAll());
    }

    @PostMapping("/api/admin/friend-links")
    @Operation(summary = "新增友链")
    public Result<FriendLink> create(@RequestBody FriendLink friendLink) {
        return Result.success("创建成功", friendLinkService.create(friendLink));
    }

    @PutMapping("/api/admin/friend-links/{id}")
    @Operation(summary = "编辑友链")
    public Result<FriendLink> update(@PathVariable Long id, @RequestBody FriendLink friendLink) {
        return Result.success("更新成功", friendLinkService.update(id, friendLink));
    }

    @DeleteMapping("/api/admin/friend-links/{id}")
    @Operation(summary = "删除友链")
    public Result<Void> delete(@PathVariable Long id) {
        friendLinkService.delete(id);
        return Result.success("删除成功", null);
    }
}
