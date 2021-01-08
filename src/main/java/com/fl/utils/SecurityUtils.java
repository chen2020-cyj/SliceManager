package com.fl.utils;


import com.fl.exception.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * @Author : 傅化韩
 * @create 2020/11/2 16:24
 */
public class SecurityUtils {
    /*
    * 获取当前用户信息
    *
    * */
    public static UserDetails getCurrentUser(){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null){
            throw  new BadRequestException(HttpStatus.UNAUTHORIZED,"当前状态已经过期");
        }
        if (authentication.getPrincipal() instanceof UserDetails){
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            UserDetailsService userDetailsService  = SpringContextHolder.getBean(UserDetailsService.class);
//            return userDetails;
            return userDetailsService.loadUserByUsername(userDetails.getUsername());
        }
        throw new BadRequestException(HttpStatus.UNAUTHORIZED, "找不到当前登录的信息");
    }

    /**
     * 获取系统用户ID
     * @return 系统用户ID
     */
//    public static Integer getCurrentUserId() {
//        UserDetails userDetails = getCurrentUser();
//        return new JSONObject(new JSONObject(userDetails).get("user")).get("id", Integer.class);
//    }

    /**
     * 获取系统用户用户名
     * @return 系统用户用户名
     */
//    public static String getCurrentUsername() {
//        UserDetails userDetails = getCurrentUser();
//        return new JSONObject(new JSONObject(userDetails).get("user")).get("username", String.class);
//    }
}
