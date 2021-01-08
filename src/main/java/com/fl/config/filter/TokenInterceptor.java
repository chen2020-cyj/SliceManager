package com.fl.config.filter;




import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.filter.TokenFilter;
import com.fl.config.security.TokenProvider;
import com.fl.entity.User;
import com.fl.service.UserService;

import com.fl.utils.JwtUtils;
import com.fl.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;



public class TokenInterceptor extends GenericFilterBean {

    private Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

    private final TokenProvider tokenProvider;

    private String urls[] = {
            "/deal/login",
            "/deal/updateToken",
            "/deal/signOut",
            "/deal/distributionTask",
            "/deal/taskState",
            "/deal/token",
            "/deal/distributionTask",
            "/deal/taskState",
            "/deal/picUpload",
    };
    @Autowired
    private UserService userService;
    public TokenInterceptor(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

//    @Override
//    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
//
//        String url = httpServletRequest.getRequestURI();
//        String token = httpServletRequest.getHeader("token");
//
//        String method = httpServletRequest.getMethod();
//        System.out.println(token);
//
//        if (!method.equals("OPTIONS")){
//            logger.info(token);
//            logger.info(url);
//            logger.info(method);
//            // 遍历需要忽略拦截的路径
//            for (String item : this.urls){
//                if (item.equals(url)){
//                    return true;
//                }
//            }
//            Integer verify1 = JwtUtils.verify(token);
//            if (!String.valueOf(verify1).equals("0")){
//                User userByToken = TokenUtils.getToken(String.valueOf(verify1));
////                Integer verify = JwtUtils.verify(token);
////                if (userByToken != null){
////                    Integer verify = JwtUtils.verify(.getToken());
////                    if (String.valueOf(verify).equals("0")) {
////
////
////                    }else {
////                        return true;
////                    }
////                }
//
//                if (token.contains(userByToken.getToken())){
//                    return true;
//                }else {
//                    httpServletResponse.setCharacterEncoding("UTF-8");
//                    httpServletResponse.setContentType("application/json; charset=utf-8");
//                    PrintWriter out = null;
//                    try {
//                        ResData res = new ResData();
//                        res.setCode(2);
//                        res.setData("404");
//                        res.setMsg("账号被占用");
//                        String json = GsonUtils.toJson(res);
//                        httpServletResponse.setContentType("application/json");
//                        out = httpServletResponse.getWriter();
//                        // 返回json信息给前端
//                        out.append(json);
//                        out.flush();
//                        return false;
//
//                    } catch (Exception e) {
////                    e.printStackTrace();
//                        httpServletResponse.sendError(500);
//                        return false;
//                    }
//                }
////                return true;
//            }else if (String.valueOf(verify1).equals("0")){
////
//                System.out.println("token过期拦截");
//                httpServletResponse.setCharacterEncoding("UTF-8");
//                httpServletResponse.setContentType("application/json; charset=utf-8");
//                PrintWriter out = null;
//                try {
//                    ResData res = new ResData();
//                    res.setCode(2);
//                    res.setData("403");
//                    res.setMsg("token过期");
//                    String json = GsonUtils.toJson(res);
//                    httpServletResponse.setContentType("application/json");
//                    out = httpServletResponse.getWriter();
//                    // 返回json信息给前端
//                    out.append(json);
//                    out.flush();
//                    return false;
//
//                } catch (Exception e) {
////                    e.printStackTrace();
//                    httpServletResponse.sendError(500);
//                    return false;
//                }
//            }
////            User userByToken = userService.getUserByToken(token);
//        }
//        return false;
////        return true;
//    }
//
//    @Override
//    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
////        HandlerInterceptor.super.postHandle(httpServletRequest, httpServletResponse, o, modelAndView);
//    }
//
//    @Override
//    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
//        // System.out.println("视图渲染之后的操作");
////        HandlerInterceptor.super.afterCompletion(httpServletRequest, httpServletResponse, o, e);
//    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String token = httpServletRequest.getHeader("token");
        String uri = httpServletRequest.getServletPath();
//        System.out.println(uri);
//        System.out.println(token);
        if (token != null){
            String verify = JwtUtils.tokenInfo(token,"userId");
            System.out.println(verify);
            User user = null;
            try {
                user = TokenUtils.getToken(verify);
            }catch (Exception e){

            }
            if (user != null){
                System.out.println("进来");
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
//        Integer verify = JwtUtils.verify(token);
//        com.fl.entity.User loginUser = TokenUtils.getToken(String.valueOf(verify));


//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
//        User user = TokenUtils.getToken(String.valueOf(verify));
//        if (user !=null && StringUtils.hasText(token)){
//        }
    }
}
