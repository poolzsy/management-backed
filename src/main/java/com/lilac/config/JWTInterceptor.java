package com.lilac.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.lilac.entity.Account;
import com.lilac.enums.HttpsCodeEnum;
import com.lilac.exception.SystemException;
import com.lilac.service.AdminService;
import com.lilac.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JWTInterceptor implements HandlerInterceptor {
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if(StringUtil.isBlank(token)){
            token = request.getParameter("token");
        }
        if (StringUtil.isBlank(token)) {
            throw new SystemException(HttpsCodeEnum.USER_NOT_LOGIN);
        }
        Account account = null;
        try {
            String audience = JWT.decode(token).getAudience().get(0);
            String[] split = audience.split("-");
            String userId = split[0];
            String role = split[1];
            if(role.equals("admin")){
                account = adminService.selectById(userId);
            }else if (role.equals("user")){
                account = userService.selectById(userId);
            }
        } catch (Exception e) {
            throw new SystemException(HttpsCodeEnum.USER_NOT_LOGIN);
        }
        if (account == null) {
            throw new SystemException(HttpsCodeEnum.USER_NOT_LOGIN);
        }
        try {
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(account.getPassword())).build();
            jwtVerifier.verify(token);
        } catch (Exception e) {
            throw new SystemException(HttpsCodeEnum.USER_NOT_LOGIN);
        }
        return true;
    }
}
