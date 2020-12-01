package com.fl.control;

import com.fl.model.clientReq.UpdateToken;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResToken;
import com.fl.entity.User;
import com.fl.model.clientReq.LoginUser;
import com.fl.service.UserService;
import com.fl.utils.GsonUtils;
import com.fl.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
@Api(tags = "用户登陆注册接口")
@RestController
public class LoginController {

    private GsonUtils gson = new GsonUtils();
    private Map<String,String> map = new HashMap<>();
    private ResData res = new ResData();
    private String data = "";
    private ResToken resToken = new ResToken();
    private User userInfo = new User();
    @Autowired
    UserService userService;

    @ApiOperation("登陆接口")
    @PostMapping("/login")
    public String login(@RequestBody LoginUser user) {

//        User user = gson.fromJson(str, User.class);


        map.put("username", user.getUsername());
        map.put("password", user.getPassword());
        System.out.println(map);
        User login = userService.login(map);
        if (login != null) {
            if (!login.getPassword().equals("")) {

                String token = JwtUtils.sign(login.getId());
                resToken.setUserId(login.getId());
                resToken.setToken(token);
                resToken.setExpiration(String.valueOf((System.currentTimeMillis()+JwtUtils.EXPIRE_TIME)/1000));

                res.setCode(20000);
                res.setMsg("success");
                res.setData(resToken);

                userInfo.setUsername(user.getUsername());
                userInfo.setPassword(user.getPassword());
                userInfo.setTokenTime(resToken.getExpiration());
                userInfo.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
                userInfo.setToken(token);
                userService.updateToken(userInfo);


                return gson.toJson(res);
            } else {
                res.setCode(1);
                res.setMsg("err");
                res.setData("");


                return gson.toJson(res);
            }
        } else {
            res.setCode(1);
            res.setMsg("err");
            res.setData("");


            return gson.toJson(res);
        }

    }
    @ApiOperation("注册接口")
    @PostMapping("/register")
    public String register(@RequestBody LoginUser user){


        map.put("username", user.getUsername());
        map.put("password", user.getPassword());
        User login = userService.login(map);
//        System.out.println(login);
        if (login == null){
            res.setCode(0);
            res.setMsg("success");
            res.setData("");


            userInfo.setPassword(user.getPassword());
            userInfo.setUsername(user.getUsername());
            System.out.println(userInfo);
            userService.register(userInfo);
            map.clear();
            return gson.toJson(res);
        }else {
            res.setCode(1);
            res.setMsg("err");
            res.setData("");
            map.clear();
            return gson.toJson(res);
        }
    }
    @ApiOperation("刷新token接口")
    @PostMapping(value = "/updateToken",produces = "application/json;charset=UTF-8")
    public String updateToken(@RequestBody UpdateToken user){


        map.put("username", user.getUsername());
        map.put("token", user.getToken());
        User login = userService.login(map);
//        System.out.println(login);
        if (login != null){
            String token = JwtUtils.sign(login.getId());

            long currentTime = System.currentTimeMillis()/1000;
            login.setToken(token);
            login.setTokenTime(String.valueOf((currentTime+JwtUtils.EXPIRE_TIME)/1000));
            userService.updateToken(login);

            resToken.setUserId(login.getId());
            resToken.setToken(token);
            resToken.setExpiration(String.valueOf((currentTime+JwtUtils.EXPIRE_TIME)/1000));

            res.setCode(0);
            res.setMsg("success");
            res.setData(resToken);

            return gson.toJson(res);
        }else {
            res.setCode(1);
            res.setMsg("没有对应的数据");
            res.setData("");
            return gson.toJson(res);
        }
    }

}

