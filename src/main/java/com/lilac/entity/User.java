package com.lilac.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends Account{

    private Integer id;
    //用户名
    private String username;
    //密码
    private String password;
    //姓名
    private String name;
    //联系方式
    private String phone;
    //邮箱
    private String email;
    //角色
    private String role;
    private String token;

}

