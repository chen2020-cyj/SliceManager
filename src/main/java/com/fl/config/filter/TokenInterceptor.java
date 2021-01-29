package com.fl.config.filter;




import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.filter.TokenFilter;
import com.fl.config.security.TokenProvider;
import com.fl.entity.User;
import com.fl.service.UserService;

import com.fl.utils.JwtUtils;
import com.fl.utils.PowerUtils;
import com.fl.utils.TokenUtils;
import io.jsonwebtoken.ExpiredJwtException;
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
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TokenInterceptor extends GenericFilterBean {

    private Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

    private final TokenProvider tokenProvider;

    public TokenInterceptor(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String token = httpServletRequest.getHeader("token");
        String uri = httpServletRequest.getServletPath();

        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

//        System.out.println(token);
        if (uri.equals("/login")){
//            System.out.println("进来了");
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }else if (uri.equals("/updateToken")){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }else if (uri.equals("/picUpload")){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }else if (uri.equals("/signOut")){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }

        if (token != null){

            String verify = JwtUtils.tokenInfo(token,"userId");

            User user = null;
            try {
//                if (verify == null){
//                    List<String> list = new ArrayList<>();
//                    list.get(0);
//                }
                user = TokenUtils.getToken(verify);

                if (!token.contains(user.getToken())){
                    System.out.println("给异常");
                    Integer d =1/0;
                }
            }catch (ArithmeticException e) {
                servletRequest.setAttribute("filter.error", "token.err");
                servletRequest.getRequestDispatcher("/tokenError").forward(servletRequest, servletResponse);
            }catch (IndexOutOfBoundsException e){
                servletRequest.setAttribute("filter.error", "token.err");
                servletRequest.getRequestDispatcher("/tokenOverTime").forward(servletRequest, servletResponse);
            }
//            catch (Exception e){
//
////                e.printStackTrace();
////                logger.error(e.getMessage());
//            }
            if (user != null){
                String power = PowerUtils.getPower(user.getUsername());
                Authentication authentication = tokenProvider.getAuthentication(power);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);

    }
}
