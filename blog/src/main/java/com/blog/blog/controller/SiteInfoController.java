package com.blog.blog.controller;

import com.blog.blog.common.Result;
import com.blog.blog.service.SiteSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/site-info")
@RequiredArgsConstructor
@Tag(name = "站点信息接口", description = "站点基本信息")
public class SiteInfoController {

    private final SiteSettingService siteSettingService;

    @GetMapping
    @Operation(summary = "获取站点基本信息")
    public Result<Map<String, String>> getSiteInfo() {
        Map<String, String> settings = siteSettingService.getAllSettings();
        return Result.success(Map.of(
                "title", settings.getOrDefault("site_title", "My Blog"),
                "description", settings.getOrDefault("site_description", "个人博客系统")
        ));
    }
}
