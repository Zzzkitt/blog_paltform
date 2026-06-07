package com.blog.blog.controller;

import com.blog.blog.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "测试接口", description = "用于验证服务可用性")
public class PingController {

    @GetMapping("/ping")
    @Operation(summary = "Ping接口", description = "返回pong，验证服务可用性")
    public Result<String> ping() {
        return Result.success("pong");
    }
}
