package com.lilac.exception;

import com.lilac.entity.Result;
import com.lilac.enums.HttpsCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理自定义的业务异常
     *
     * @param e 业务异常
     * @return Result
     */
    @ExceptionHandler(SystemException.class)
    public Result<Void> handleSystemException(SystemException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理所有未被捕获的未知异常
     *
     * @param e 未知异常
     * @return Result
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("未捕获的系统异常！ ", e);
        // 对外屏蔽内部细节，返回统一的系统错误提示
        return Result.error(HttpsCodeEnum.SYSTEM_ERROR);
    }
}