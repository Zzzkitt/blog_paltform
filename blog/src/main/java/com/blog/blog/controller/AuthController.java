package com.blog.blog.controller;

import com.blog.blog.common.Result;
import com.blog.blog.dto.JwtResponse;
import com.blog.blog.dto.LoginRequest;
import com.blog.blog.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证接口", description = "用户登录、注册等认证相关接口")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录接口，返回JWT令牌")
    public Result<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        JwtResponse jwtResponse = authService.login(request);
        return Result.success("登录成功", jwtResponse);
    }
}
