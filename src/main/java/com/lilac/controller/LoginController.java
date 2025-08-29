package com.lilac.controller;

import com.lilac.entity.Account;
import com.lilac.entity.DTO.RegisterDTO;
import com.lilac.entity.Result;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.exception.SystemException;
import com.lilac.service.AdminService;
import com.lilac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;

    /**
     * 登录
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        Account dbaccount = null;
        if (account.getRole().equals("admin")) {
            dbaccount = adminService.login(account);
        } else if(account.getRole().equals("user")){
            dbaccount = userService.login(account);
        } else {
            throw new SystemException(HttpsCodeEnum.BAD_REQUEST);
        }
        return Result.success(dbaccount);
    }

    /**
     * 注册
     * @return 注册结果
     */
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }
}
