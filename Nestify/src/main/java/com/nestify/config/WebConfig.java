package com.nestify.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.nestify.interceptor.LoginCheckInterceptor;
import com.nestify.interceptor.UserCheckInterceptor;
import com.nestify.service.NestService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final NestService nestService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // LoginCheckInterceptor 등록
        registry.addInterceptor(new LoginCheckInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/signup", "/signin", "/main", "/css/**", "/images/**", "/js/**", "/error");

        // UserCheckInterceptor 등록
        registry.addInterceptor(new UserCheckInterceptor(nestService))
                .order(2)
                .addPathPatterns("/api/v1/**");
    }
}
