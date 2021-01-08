package com.fl.control;

import com.fl.aop.annotation.Log;
import com.fl.config.bean.JwtUserDto;
import com.fl.entity.Menu;
import com.fl.entity.Rule;
import com.fl.model.Authority;
import com.fl.model.clientReq.*;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResToken;
import com.fl.entity.User;
import com.fl.service.MenuService;
import com.fl.service.RuleUserInfoService;
import com.fl.service.UserService;
import com.fl.utils.GsonUtils;
import com.fl.utils.JwtUtils;
import com.fl.utils.RedisHelper;
import com.fl.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    @Autowired
    private RuleUserInfoService ruleUserInfoService;
    @Autowired
    private MenuService menuService;



    @ApiOperation("登陆接口")
    @PostMapping(value = "/login", produces = "application/json;charset=UTF-8")
    public String login(@RequestBody LoginUser user) {
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
            String token = JwtUtils.sign(login.getId(), GsonUtils.toJson(authentication), login.getUsername());

            resToken.setUserId(login.getId());
            resToken.setToken(token);
            resToken.setExpiration(String.valueOf((System.currentTimeMillis() + JwtUtils.EXPIRE_TIME) / 1000));

//            TokenUtils.setToken(login,token);

            User userMsg = TokenUtils.getToken(String.valueOf(login.getId()));

            if (userMsg != null) {

                RedisHelper.del("token-" + userMsg.getId());
                TokenUtils.setToken(login, token);
            } else {

                TokenUtils.setToken(login, token);
            }
            List<Authority> authorities = new ArrayList<>();

            List<Menu> list = menuService.selectByRoleId(login);
//            System.out.println(list);

            for (int i = 0; i < list.size(); i++) {
                Authority authority = new Authority();
                if (list.get(i).getWhetherMenu() == 1) {
                    authority.setId(list.get(i).getId());
                    authority.setIcon(list.get(i).getIcon());
                    authority.setMainMenu(list.get(i).getTitle());
                    authority.setMainMenuUrl(list.get(i).getPath());
                    authorities.add(authority);
                }
            }

            for (int i = 0; i < authorities.size(); i++) {
                List<String> subMain = new ArrayList<>();
                for (int i1 = 0; i1 < list.size(); i1++) {
                    if (authorities.get(i).getId() == list.get(i1).getPid()){
                        subMain.add(list.get(i1).getPath());
                    }
                }
                if (subMain.size() == 0){
                    authorities.get(i).setList("");
                }else {
                    authorities.get(i).setList(subMain);
                }


            }
            resToken.setMenu(authorities);

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
//        User user = gson.fromJson(str, User.class);


    }
    @Log("user:register")
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
//    @Log("user:register")
    @ApiOperation("刷新token接口")
    @PostMapping(value = "/updateToken",produces = "application/json;charset=UTF-8")
    public String updateToken(@RequestBody UpdateToken user) {


        User login = userService.selectUserInfo(user.getUserId());
        User cacheUser = TokenUtils.getToken(String.valueOf(user.getUserId()));

        String auth = JwtUtils.tokenInfo(cacheUser.getToken(), "auth");
        List<String> list = new ArrayList<>();
        RedisHelper.del("token-"+user.getUserId());
        if (login != null){
            String token = JwtUtils.sign(login.getId(),auth,login.getUsername());

            long currentTime = System.currentTimeMillis();
            login.setToken(token);
            login.setTokenTime(String.valueOf((currentTime + JwtUtils.EXPIRE_TIME) / 1000));
            login.setUpdateTime(String.valueOf(System.currentTimeMillis() / 1000));
//            userService.updateToken(login);

            TokenUtils.setToken(login,token);

            resToken.setUserId(login.getId());
            resToken.setToken(token);
            resToken.setExpiration(String.valueOf((currentTime + JwtUtils.EXPIRE_TIME) / 1000));

            res.setCode(0);
            res.setMsg("success");
            res.setData(resToken);

            return gson.toJson(res);
        }else {
            res.setCode(1);
            res.setMsg("这个id没有对应的数据");
            res.setData("");

            return gson.toJson(res);
        }

    }
    @Log("user:register")
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
        List<Rule> rules = ruleUserInfoService.selectByRoles();

            res.setCode(0);
            res.setMsg("success");
            res.setData(rules);

            return res;

    }
    @Log("user:signOut")
    @ApiOperation("退出登陆")
    @PostMapping(value = "/signOut",produces = "application/json;charset=UTF-8")
    public ResData revise(@RequestBody SignOut signOut){

//        Integer userId = JwtUtils.verify(signOut.getToken());



        User user = userService.selectUserInfo(signOut.getId());

        RedisHelper.del("token-"+user.getId());
        System.out.println(user);
        user.setTokenTime("");
        user.setToken("");
//        userService.updateToken(user);

        res.setCode(0);
        res.setMsg("success");
        res.setData("");

        return res;
    }
}

