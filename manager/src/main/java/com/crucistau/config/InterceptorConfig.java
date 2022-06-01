package com.crucistau.config;

import com.crucistau.commons.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 注册token拦截器 new JwtInterceptor
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())    //添加自定义拦截器
                .addPathPatterns("/**")         //拦截所有的请求，通过判断token是否合法来决定是否登录(默认拦截所有，这一行可以没有 )
                .excludePathPatterns("/**/login","/student/rePassword","/**/export","/**/import");     //排除登录注册接口



        //注册TestInterceptor拦截器
//        InterceptorRegistration registration = registry.addInterceptor(jwtInterceptor());
//        registration.addPathPatterns("/**");                      //添加拦截路径
//        registration.excludePathPatterns(                         //添加不拦截路径
//            "/**/*.html",            //html静态资源
//            "/**/*.js",              //js静态资源
//            "/**/*.css",             //css静态资源
//            "/**/*.woff",
//            "/**/*.ttf",
//            "/swagger-ui.html"
    }

    /**
     * 将自定义的token拦截器注入为bean  放到  WebMvcConfigurer
     * @return
     */
    @Bean
    public JwtInterceptor jwtInterceptor(){
        return new JwtInterceptor();
    }
}
