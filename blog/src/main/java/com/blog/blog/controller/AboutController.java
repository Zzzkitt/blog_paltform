package com.blog.blog.controller;

import com.blog.blog.common.Result;
import com.blog.blog.entity.AboutInfo;
import com.blog.blog.repository.AboutRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "关于页面接口")
public class AboutController {

    private final AboutRepository aboutRepository;

    @GetMapping("/api/about")
    @Operation(summary = "获取关于页面（公开）")
    public Result<AboutInfo> getAbout() {
        return aboutRepository.findById(1L)
                .map(Result::success)
                .orElse(Result.success("暂无内容", null));
    }

    @PutMapping("/api/admin/about")
    @Operation(summary = "更新关于页面（管理端）")
    public Result<AboutInfo> updateAbout(@RequestBody AboutInfo aboutInfo) {
        AboutInfo about = aboutRepository.findById(1L).orElse(new AboutInfo());
        about.setContent(aboutInfo.getContent());
        return Result.success("更新成功", aboutRepository.save(about));
    }
}
