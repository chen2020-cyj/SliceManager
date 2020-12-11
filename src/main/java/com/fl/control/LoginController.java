package com.fl.control;

import com.fl.entity.Rule;
import com.fl.model.clientReq.*;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResToken;
import com.fl.entity.User;
import com.fl.service.RuleUserInfoService;
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

    private ResData res = new ResData();
    private String data = "";
    private ResToken resToken = new ResToken();
    private User userInfo = new User();
    @Autowired
    private UserService userService;
    @Autowired
    private RuleUserInfoService ruleUserInfoService;

    @ApiOperation("登陆接口")
    @PostMapping("/login")
    public String login(@RequestBody LoginUser user) {

//        User user = gson.fromJson(str, User.class);

        Map<String,String> map = new HashMap<>();
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

                res.setCode(0);
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
    public ResData register(@RequestBody Register user){


        User login = userService.selectByUser(user.getUsername());

        if (login == null){


                res.setCode(0);
                res.setMsg("success");
                res.setData("");

                Rule rule = ruleUserInfoService.selectUser(user.getGroupId());
                userInfo.setPassword(user.getPassword());
                userInfo.setUsername(user.getUsername());
                userInfo.setName(user.getName());
                userInfo.setGroupId(rule.getId());
//            userInfo.set
                System.out.println(userInfo);
                userService.register(userInfo);
                return res;

        }else {
            res.setCode(1);
            res.setMsg("账号已经存在");
            res.setData("");

            return res;
        }
    }
    @ApiOperation("刷新token接口")
    @PostMapping(value = "/updateToken",produces = "application/json;charset=UTF-8")
    public String updateToken(@RequestBody UpdateToken user){

        User login = userService.selectUserInfo(user.getUserId());
//        System.out.println(login);
        if (login != null){
            String token = JwtUtils.sign(login.getId());

            long currentTime = System.currentTimeMillis();
            login.setToken(token);
            login.setTokenTime(String.valueOf((currentTime+JwtUtils.EXPIRE_TIME)/1000));
            login.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
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

    @ApiOperation("修改密码")
    @PostMapping(value = "/revise",produces = "application/json;charset=UTF-8")
    public ResData revise(@RequestBody ReqRevise reqRevise){



        return res;
    }
    @ApiOperation("查询所有权限组")
    @PostMapping(value = "/findAllRoles",produces = "application/json;charset=UTF-8")
    public ResData revise(){

//        User user = userService.selectUserInfo(reqAllRoles.getUserId());  @RequestBody ReqAllRoles reqAllRoles
//        long currentTime = System.currentTimeMillis()/1000;
//        long tokenTime = Long.valueOf(user.getTokenTime());
        Rule rule = ruleUserInfoService.selectByRoles();

            res.setCode(0);
            res.setMsg("success");
            res.setData(rule);

            return res;

    }
    @ApiOperation("退出登陆")
    @PostMapping(value = "/signOut",produces = "application/json;charset=UTF-8")
    public ResData revise(@RequestBody SignOut signOut){

        Integer userId = JwtUtils.verify(signOut.getToken());



        User user = userService.selectUserInfo(userId);
        System.out.println(user);
        user.setTokenTime(" ");
        user.setToken(" ");
        userService.updateToken(user);

        res.setCode(0);
        res.setMsg("success");
        res.setData("");

        return res;
    }
}

