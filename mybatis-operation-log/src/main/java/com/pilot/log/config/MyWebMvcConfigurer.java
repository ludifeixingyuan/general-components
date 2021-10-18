package com.pilot.log.config;

import com.pilot.log.interceptor.OpChangeLogInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * web mvc配置
 *
 * @author ludifeixingyuan
 * @date 2021-10-18
 */
@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getHandlerInterceptor());
    }

    public HandlerInterceptor getHandlerInterceptor() {
        return new OpChangeLogInterceptor();
    }
}
