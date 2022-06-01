package com.crucistau.commons.interceptor;


import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.crucistau.commons.Constants;
import com.crucistau.entity.Manage;
import com.crucistau.entity.Student;
import com.crucistau.entity.Teacher;
import com.crucistau.exception.ServiceException;
import com.crucistau.service.impl.ManageServiceImpl;
import com.crucistau.service.impl.StuServiceImpl;
import com.crucistau.service.impl.TeacherServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 拦截器，拦截token (这里仅作设定)
 */

public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private StuServiceImpl ssI;
    @Autowired
    private TeacherServiceImpl teacherService;

    @Autowired
    private ManageServiceImpl manageService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServiceException {
        String token = request.getHeader("token");
        //如果不是映射方法直接跳过
        if( !(handler instanceof HandlerMethod)){
            return true;
        }
        //执行认证
        if(StrUtil.isBlank(token)){
            throw new ServiceException(Constants.CODE_401,"无token,请重新登录");
        }
        //获取token中的user Id 与identity
        String userId;
        try{
            userId = JWT.decode(token).getAudience().get(0);
        }catch (JWTDecodeException j){
            throw new ServiceException(Constants.CODE_401, "token验证失败");
        }
        String identity;
        try {
            identity = JWT.decode(token).getClaim("identity").asString();
        }catch (JWTDecodeException j){
            throw new ServiceException(Constants.CODE_401, "token验证失败");
        }
        if (identity.equals("ROLE_STUDENT")){
            //根据token中的userId查询数据库
            Student stu = ssI.getById(userId);//主键ID
            if (stu == null){
                throw new ServiceException(Constants.CODE_401, "用户不存在，请重新登录");
            }
            //用户密码加签验证token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(stu.getStuPwd())).build();
            try {
                jwtVerifier.verify(token);//验证token
            } catch (JWTVerificationException e) {
                throw new ServiceException(Constants.CODE_401, "token验证失败，请重新登录");
            }
        }else if(identity.equals("ROLE_TEACHER")){
            Teacher tea = teacherService.getById(userId);
            if (tea ==null){
                throw new ServiceException(Constants.CODE_401, "用户不存在，请重新登录");
            }
            //用户密码加签验证token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(tea.getTeaPwd())).build();
            try {
                jwtVerifier.verify(token);//验证token
            } catch (JWTVerificationException e) {
                throw new ServiceException(Constants.CODE_401, "token验证失败，请重新登录");
            }
        }else{
            //根据token中的userId查询数据库
            Manage man = manageService.getById(userId);//主键ID
            if (man == null){
                throw new ServiceException(Constants.CODE_401, "用户不存在，请重新登录");
            }
            //用户密码加签验证token
            JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(man.getManPwd())).build();
            try {
                jwtVerifier.verify(token);//验证token
            } catch (JWTVerificationException e) {
                throw new ServiceException(Constants.CODE_401, "token验证失败，请重新登录");
            }
        }

        return true;
    }
}
