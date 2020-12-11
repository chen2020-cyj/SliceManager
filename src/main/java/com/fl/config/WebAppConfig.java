package com.fl.config;

import com.fl.config.filter.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebAppConfig implements WebMvcConfigurer {

//    @Autowired
//    private CROSInterceptor crosInterceptor;

    @Autowired
    private TokenInterceptor tokenInterceptor;
    @Bean
    public TokenInterceptor getAuthInterceptor() {
        return new TokenInterceptor();
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry){
//        registry.addInterceptor(crosInterceptor).addPathPatterns("/**");   // 跨域拦截器
        registry.addInterceptor(getAuthInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/error","/csrf", "/swagger-ui.html/**");  // token 验证拦截器

    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
//        WebMvcConfigurer.super.addResourceHandlers(registry);
    }
}
