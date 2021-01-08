package com.fl.config;

import com.fl.config.filter.TokenInterceptor;
import com.fl.config.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@RequiredArgsConstructor
//@Configuration
public class WebAppConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

//    @Autowired
//    private CROSInterceptor crosInterceptor;


//    private TokenInterceptor tokenInterceptor;
//    @Bean
//    public TokenInterceptor getAuthInterceptor() {
//        return new TokenInterceptor();
//    }
//    @Override
//    public void addInterceptors(InterceptorRegistry registry){
////        registry.addInterceptor(crosInterceptor).addPathPatterns("/**");   // 跨域拦截器
//        registry.addInterceptor(getAuthInterceptor()).addPathPatterns("/**")
//                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/error","/csrf", "/swagger-ui.html/**");  // token 验证拦截器
//
//    }
    private final TokenProvider tokenProvider;


    @Override
    public void configure(HttpSecurity http) throws Exception {
        TokenInterceptor customFilter = new TokenInterceptor(tokenProvider);
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("swagger-ui.html")
//                .addResourceLocations("classpath:/META-INF/resources/");
//        registry.addResourceHandler("/webjars/**")
//                .addResourceLocations("classpath:/META-INF/resources/webjars/");
////        registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/").setCachePeriod(0);
////        WebMvcConfigurer.super.addResourceHandlers(registry);
//    }

}
