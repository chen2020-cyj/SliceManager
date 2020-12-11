package com.fl.control;


import com.fl.entity.Rule;
import com.fl.entity.User;
import com.fl.model.clientReq.TestUserInfo;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientReq.Roles;
import com.fl.service.RuleUserInfoService;
import com.fl.service.UserService;
import com.fl.utils.GsonUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "权限接口")
@RestController
public class RuleController {

    @Autowired
    RuleUserInfoService ruleUserInfoService;
    @Autowired
    UserService userService;


    private static Map<String,String> map = new HashMap<>();
    private static ResData res = new ResData();
    @PostMapping("/getInfo")
    public ResData getInfo(@RequestBody TestUserInfo testUserInfo){

        User user = userService.selectUserInfo(testUserInfo.getId());
        Roles info = new Roles();
        Rule rule = ruleUserInfoService.selectUser(user.getGroupId());
        List<String> list = new ArrayList<>();

        list.add(rule.getRoles());

        info.setName(user.getName());
        info.setRoles(list);
        res.setCode(0);
        res.setMsg("success");
        res.setData(info);

        return res;
    }

//    @PostMapping("/testLogin")
//    public String testLogin(@RequestBody LoginUser user){
//
//        map.put("username",user.getUsername());
//        map.put("password",user.getPassword());
//        userInfoService.selectUser(map);
//
//        res.setCode(0);
//        res.setMsg("success");
//        res.setData("");
//        return GsonUtils.toJson(res);
//
//    }
}
