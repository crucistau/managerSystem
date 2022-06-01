package com.crucistau.utils;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.crucistau.entity.Student;
import com.crucistau.service.IStuService;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class TokenUtils {

    private static IStuService staticUserService;

    @Resource
    private IStuService userService;

    @PostConstruct
    public void setUserService() {
        staticUserService = userService;
    }

    /**
     * 生成Token
     * @return
     */
    public static  String  getToken(String userId, String identity,String password){
        return JWT.create().withAudience(userId)    // 将 user id 保存到 token 里面,作为载荷
                .withClaim("identity",identity)
                .withExpiresAt(DateUtil.offsetHour(new Date(),1))// 1小时后token过期
                .sign(Algorithm.HMAC256(password)); // 以 password 作为 token 的密钥
    }

    /**
     * 获取当前登录的用户信息
     *
     * @return user对象
     */
    public static Student getCurrentUser() {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String token = request.getHeader("token");
            if (StrUtil.isNotBlank(token)) {
                String userId = JWT.decode(token).getAudience().get(0);

                return staticUserService.getById(Integer.valueOf(userId));
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
