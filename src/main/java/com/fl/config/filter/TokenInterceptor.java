package com.fl.config.filter;

import com.fl.entity.User;
import com.fl.model.clientRes.ResData;
import com.fl.service.UserService;
import com.fl.utils.GsonUtils;
import com.fl.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    private Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);
    private String urls[] = {
            "/deal/**",
//            "/deal/updateToken",
//            "/deal/signOut",
//            "/deal/distributionTask",
//            "/deal/taskState",
//            "/deal/token"
    };
    @Autowired
    private UserService userService;


    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

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
//            // 查询验证token
//            User userByToken = userService.getUserByToken(token);
//
//            if (userByToken != null){
//                Integer verify = JwtUtils.verify(userByToken.getToken());
//                if (String.valueOf(verify).equals("0")) {
//                    System.out.println("token过期拦截");
//                    httpServletResponse.setCharacterEncoding("UTF-8");
//                    httpServletResponse.setContentType("application/json; charset=utf-8");
//                    PrintWriter out = null;
//                    try {
//                        ResData res = new ResData();
//                        res.setCode(2);
//                        res.setData("403");
//                        res.setMsg("token过期");
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
//
//                }else {
//                    return true;
//                }
//            }
//
//        }
//        return false;
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
//        HandlerInterceptor.super.postHandle(httpServletRequest, httpServletResponse, o, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        // System.out.println("视图渲染之后的操作");
//        HandlerInterceptor.super.afterCompletion(httpServletRequest, httpServletResponse, o, e);
    }

}
