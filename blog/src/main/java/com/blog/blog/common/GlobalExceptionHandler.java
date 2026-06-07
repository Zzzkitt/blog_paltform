package com.blog.blog.common;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /*参数校验异常*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("参数校验失败 [{}]: {}", request.getRequestURI(), message);
        return Result.error(400, message);
    }

    /*实体未找到异常*/
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result<Void> handleNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        log.warn("资源不存在 [{}]: {}", request.getRequestURI(), ex.getMessage());
        return Result.error(404, "请求的资源不存在");
    }

    /*无权限访问异常*/
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<Void> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        log.warn("权限不足 [{}]", request.getRequestURI());
        return Result.error(403, "权限不足，无法访问");
    }

    /*认证失败异常（用户名或密码错误）*/
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<Void> handleBadCredentials(BadCredentialsException ex, HttpServletRequest request) {
        log.warn("登录认证失败 [{}]: {}", request.getRequestURI(), ex.getMessage());
        return Result.error(401, "用户名或密码错误");
    }

    /*参数类型不匹配*/
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("请求参数类型错误 [{}]: {}", request.getRequestURI(), ex.getMessage());
        return Result.error(400, "请求参数类型错误");
    }

    /*请求体格式错误*/
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("请求体格式错误：{} ", ex.getMessage());
        return Result.error(400, ex.getMessage());
    }

    /*业务异常*/
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBusinessException(BusinessException ex) {
        log.warn("业务异常：{} ", ex.getMessage());
        return Result.error(ex.getCode(), ex.getMessage());
    }

    /*通用异常*/
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception ex, HttpServletRequest request) {
        log.error("服务器内部错误 [{}]", request.getRequestURI(), ex);
        return Result.error(500, "服务器内部错误");
    }
}
