package com.fl.control;

import com.fl.aop.annotation.Log;
import com.fl.config.bean.JwtUserDto;
import com.fl.entity.*;
import com.fl.model.Authority;
import com.fl.model.clientReq.*;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResToken;
import com.fl.service.*;
import com.fl.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Api(tags = "用户登陆注册接口")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private GsonUtils gson = new GsonUtils();

    private ResData res = new ResData();
    private String data = "";
    private ResToken resToken = new ResToken();
    private User userInfo = new User();

    //spring-Security 认证管理器
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private UserService userService;
//    @Autowired
//    private RuleUserInfoService ruleUserInfoService;
    @Autowired
    private MenuService menuService;
    @Resource
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SysRoleRefRouteService sysRoleRefRouteService;
    @Autowired
    private RoleInfoService roleInfoService;


    @ApiOperation("登陆接口")
    @PostMapping(value = "/login", produces = "application/json;charset=UTF-8")
    public synchronized String login(@RequestBody LoginUser user) {
        try {

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            JwtUserDto jwtUserDto = (JwtUserDto) authentication.getPrincipal();

            Map<String, String> map = new HashMap<>();
            map.put("username", jwtUserDto.getUsername());
            map.put("password", jwtUserDto.getPassword());

            User login = userService.login(map);
            String token = JwtUtils.sign(login.getId());

            String power = JwtUtils.power(GsonUtils.toJson(authentication), login.getUsername());
            PowerUtils.savePower(power,login.getUsername());

            //返回给前端的token信息
            resToken.setUserId(login.getId());
            resToken.setToken(token);
            resToken.setExpiration(String.valueOf((System.currentTimeMillis() + JwtUtils.EXPIRE_TIME) / 1000));

            User userMsg = TokenUtils.getToken(String.valueOf(login.getId()));

            if (userMsg != null) {

                RedisHelper.del("token-" + userMsg.getId());
                TokenUtils.setToken(login, token);
            } else {
                TokenUtils.setToken(login, token);
            }

//            System.out.println(list);
//            resToken.setMenu(authorities);

            res.setCode(0);
            res.setMsg("success");
            res.setData(resToken);

            return gson.toJson(res);
        } catch (Exception e) {
            e.printStackTrace();
            res.setCode(1);
            res.setMsg("用户名或账号错误");
            res.setData("");

            return gson.toJson(res);
        }
    }
    @PreAuthorize("@zz.check('admin:addAdmin')")
    @Log("admin:addAdmin")
    @ApiOperation("注册接口")
    @PostMapping(value = "/register",produces = "application/json;charset=UTF-8")
    public ResData register(@RequestBody Register user) {

        User login = userService.selectByUser(user.getUsername());
//        List<User> list = new ArrayList<>();
////        userService.saveBatch(list);
        if (login == null) {
            String encode = passwordEncoder.encode(user.getPassword());
            User register = new User();

            register.setRoleId(user.getRoleId());
            register.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
            register.setUsername(user.getUsername());
            register.setName(user.getName());
            register.setGroupId(1);
            register.setTokenTime("");
            register.setToken("");
            register.setPassword(encode);

            userService.save(register);

            res.setCode(0);
            res.setMsg("success");
            res.setData("");

            return res;

        } else {
            res.setCode(1);
            res.setMsg("账号已经存在");
            res.setData("");

            return res;
        }
    }

    //    @Log("user:register")
    @ApiOperation("刷新token接口")
    @PostMapping(value = "/updateToken", produces = "application/json;charset=UTF-8")
    public String updateToken(@RequestBody UpdateToken user) {

        ResData resData = new ResData();

        User login = userService.selectUserInfo(user.getUserId());

//        String power = PowerUtils.getPower(login.getUsername());

//        User cacheUser = TokenUtils.getToken(String.valueOf(user.getUserId()));

//        String auth = JwtUtils.tokenInfo(cacheUser.getToken(), "auth");
        List<String> list = new ArrayList<>();
//        RedisHelper.del("token-" + user.getUserId());
        if (login != null) {
//            System.out.println(auth);
            RedisHelper.del("token-"+login.getId());
            String token = JwtUtils.sign(login.getId());

            long currentTime = System.currentTimeMillis();
            login.setToken(token);
            login.setTokenTime(String.valueOf((currentTime + JwtUtils.EXPIRE_TIME) / 1000));
            login.setUpdateTime(String.valueOf(System.currentTimeMillis() / 1000));
//            userService.updateToken(login);

            TokenUtils.setToken(login, token);

            resToken.setUserId(login.getId());
            resToken.setToken(token);
            resToken.setExpiration(String.valueOf((currentTime + JwtUtils.EXPIRE_TIME) / 1000));

            resData.setCode(0);
            resData.setMsg("success");
            resData.setData(resToken);

            return gson.toJson(resData);
        } else {
            resData.setCode(1);
            resData.setMsg("这个id没有对应的数据");
            resData.setData("");

            return gson.toJson(resData);
        }

    }

    @Log("user:revise")
    @ApiOperation("修改密码")
    @PostMapping(value = "/revise", produces = "application/json;charset=UTF-8")
    public ResData revise(@RequestBody ReqRevise reqRevise) {


        System.out.println(reqRevise);
        User user = userService.selectByUserId(reqRevise.getId());
        String encode = passwordEncoder.encode(reqRevise.getPassword());
        user.setPassword(encode);

        RedisHelper.del("token-"+user.getId());

        userService.updateToken(user);
        res.setCode(0);
        res.setMsg("success");
        res.setData("");
        return res;
    }

//    @ApiOperation("修改用户权限")
//    @PostMapping(value = "/select", produces = "application/json;charset=UTF-8")
//    public ResData updateUserGroup(@RequestBody ReqUpdateGroup reqUpdateGroup) {
//
//
//
//        return res;
//    }


    //    @ApiOperation("查询所有权限组")
//    @PostMapping(value = "/findAllRoles",produces = "application/json;charset=UTF-8")
//    public ResData revise(){
//
////        User user = userService.selectUserInfo(reqAllRoles.getUserId());  @RequestBody ReqAllRoles reqAllRoles
////        long currentTime = System.currentTimeMillis()/1000;
////        long tokenTime = Long.valueOf(user.getTokenTime());
//        List<Rule> rules = ruleUserInfoService.selectByRoles();
//
//            res.setCode(0);
//            res.setMsg("success");
//            res.setData(rules);
//
//            return res;
//
//    }
    @Log("user:signOut")
    @ApiOperation("退出登陆")
    @PostMapping(value = "/signOut", produces = "application/json;charset=UTF-8")
    public ResData revise(@RequestBody SignOut signOut) {

//        Integer userId = JwtUtils.verify(signOut.getToken());
        User user = userService.selectUserInfo(signOut.getId());

        RedisHelper.del("token-" + user.getId());
        RedisHelper.del("power-"+user.getUsername());
        System.out.println(user);
        user.setTokenTime("");
        user.setToken("");
//        userService.updateToken(user);

        res.setCode(0);
        res.setMsg("success");
        res.setData("");

        return res;
    }

    @ApiOperation("token异常")
    @PostMapping(value = "/tokenError", produces = "application/json;charset=UTF-8")
    public ResData tokenErr(HttpServletRequest request) {

        res.setCode(400);
        res.setMsg("该账号已被登录");
        res.setData("");

        return res;
    }
    @ApiOperation("token过期")
    @PostMapping(value = "/tokenOverTime", produces = "application/json;charset=UTF-8")
    public ResData tokenOverTime(HttpServletRequest request) {

        res.setCode(400);
        res.setMsg("token过期");
        res.setData("");

        return res;
    }
}

