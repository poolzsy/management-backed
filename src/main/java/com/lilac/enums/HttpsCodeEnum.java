package com.lilac.enums;

import lombok.Getter;

@Getter
public enum HttpsCodeEnum {
    SUCCESS(200, "操作成功"),
    SYSTEM_ERROR(500, "系统内部错误，请联系管理员"),

    BAD_REQUEST(400, "无效的请求参数"),
    USER_NAME_EXIST(40001, "用户名已存在"), // 这里应该是 CardId
    USER_CARD_ID_ERROR(40002, "会员卡号生成失败，请稍后重试或联系管理员"),
    USER_PHONE_EXIST(40003, "手机号已存在"),
    USERNAME_PHONE_EMPTY(40004, "用户名和手机号不能为空"),
    NEED_ID(40005, "更新操作必须提供用户ID"),
    USER_NOT_EXIST(40006, "该用户不存在"),
    USER_PASSWORD_ERROR(40007, "用户密码或错误"),
    USER_NOT_LOGIN(401, "未授权"),
    RESOURCE_NOT_FOUND(404, "请求的资源不存在");

    private final Integer code;
    private final String message;

    HttpsCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
