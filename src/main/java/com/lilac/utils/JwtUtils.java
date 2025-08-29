package com.lilac.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.lilac.entity.Account;
import com.lilac.service.AdminService;
import com.lilac.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;

@Component
public class JwtUtils {

    @Autowired
    AdminService adminService;
    @Autowired
    UserService userService;

    static AdminService staticAdminService;
    static UserService staticUserService;

    @PostConstruct
    public void init(){
        staticAdminService = adminService;
        staticUserService = userService;
    }

    public static String createToken(String data,String sign){
        return JWT.create().withAudience(data)
                .withExpiresAt(DateUtil.offsetDay(new Date(),1))
                .sign(Algorithm.HMAC256(sign));
    }

    public static Account getCurrentUser(){
        Account account = null;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("token");
        if(StrUtil.isBlank(token)){
            token = request.getParameter("token");
        }
        String audience = JWT.decode(token).getAudience().get(0);
        String[] split = audience.split("-");
        String userId = split[0];
        String role = split[1];
        if(role.equals("admin")){
            return staticAdminService.selectById(userId);
        }else if (role.equals("user")){
            return staticUserService.selectById(userId);
        }
        return null;
    }
}
