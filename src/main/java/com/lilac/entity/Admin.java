package com.lilac.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * (Admin)表实体类
 *
 * @author makejava
 * @since 2025-08-27 12:29:04
 */
@SuppressWarnings("serial")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {

    private Integer id;
    //用户名
    private String username;
    //密码
    private String password;
    //姓名
    private String name;
    //手机号
    private String phone;
    //邮箱
    private String email;

}

