package com.lilac.entity;

import com.lilac.enums.HttpsCodeEnum;
import lombok.Data;

@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(HttpsCodeEnum.SUCCESS.getCode(), HttpsCodeEnum.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> error(HttpsCodeEnum httpsCodeEnum) {
        return new Result<>(httpsCodeEnum.getCode(), httpsCodeEnum.getMessage(), null);
    }

    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> error(HttpsCodeEnum httpsCodeEnum, String customMessage) {
        return new Result<>(httpsCodeEnum.getCode(), customMessage, null);
    }
}