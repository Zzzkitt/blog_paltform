package com.blog.blog.common;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain=true)
@EqualsAndHashCode(exclude = {"timestamp"})
public class Result<T> {

    private int code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    private Result() {
        this.timestamp = LocalDateTime.now();
    }

    public static <T> Result <T> success(T data) {
        return new Result<T>()
                .setCode(200)
                .setMessage("success")
                .setData(data);
    }

    public static <T> Result <T> success(String message, T data) {
        return new Result<T>()
                .setCode(200)
                .setMessage(message)
                .setData(data);
    }

    public static <T> Result <T> success() {
        return success(null);
    }

    public static <T> Result <T> error(int code, String message) {
        return new Result<T>()
                .setCode(code)
                .setMessage(message)
                .setData(null);
    }

    public static <T> Result <T> error(String message) {
        return error(500, message);
    }
}
