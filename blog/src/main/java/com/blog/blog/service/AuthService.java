package com.blog.blog.service;

import com.blog.blog.dto.JwtResponse;
import com.blog.blog.dto.LoginRequest;
import com.blog.blog.security.JwtTokenProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 用户登录认证
     * @param request 登录请求（用户名、密码）
     * @return JwtResponse 包含 token、类型、过期时间、用户名、角色
     */
    public JwtResponse login(LoginRequest request) {
        //1.创建未认证的 AuthenticationToken
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        //2.调用 AuthenticationManager 进行认证
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        //3.认证通过,将认证信息存入 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //4.从任认证结果中获取用户详情和角色
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(grantedAuthority -> grantedAuthority.getAuthority().replace("ROLE_",""))
                .orElse("VISITOR");

        //5.生成 JWT token
        String token = jwtTokenProvider.generateToken(username, role);
        Long expiresIN = jwtTokenProvider.getExpirationMs();

        //6.返回响应
        return new JwtResponse(token, expiresIN, username, role);
    }
}
