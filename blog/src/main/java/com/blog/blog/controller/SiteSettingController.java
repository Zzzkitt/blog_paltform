package com.blog.blog.controller;

import com.blog.blog.common.Result;
import com.blog.blog.service.SiteSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "站点设置接口")
public class SiteSettingController {

    private final SiteSettingService siteSettingService;

    @GetMapping("/api/admin/site-settings")
    @Operation(summary = "获取全部站点设置（管理端）")
    public Result<Map<String, String>> getAll() {
        return Result.success(siteSettingService.getAllSettings());
    }

    @PostMapping("/api/admin/site-settings")
    @Operation(summary = "批量更新站点设置")
    public Result<Void> bulkUpdate(@RequestBody Map<String, String> settings) {
        siteSettingService.bulkUpdate(settings);
        return Result.success("保存成功", null);
    }
}
