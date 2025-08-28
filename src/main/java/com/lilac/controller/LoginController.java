package com.lilac.controller;

import com.lilac.entity.DTO.LoginDTO;
import com.lilac.entity.Result;
import com.lilac.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private AdminService adminService;

    /**
     * 登录
     * @return 登录结果
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        return adminService.login(loginDTO);
    }
}
