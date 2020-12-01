package com.fl.control;


import com.fl.entity.TestUserInfo;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientReq.UserInfoTest;
import com.fl.service.TestUserInfoService;
import com.fl.utils.GsonUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "测试接口")
@RestController
public class TestController {

    @Autowired
    TestUserInfoService userInfoService;

    private static Map<String,String> map = new HashMap<>();
    private static ResData res = new ResData();
    @PostMapping("/getInfo")
    public String getInfo(@RequestBody com.fl.model.clientReq.TestUserInfo testUserInfo){
        TestUserInfo userInfo = userInfoService.selectUser(testUserInfo.getId());
        UserInfoTest info = new UserInfoTest();

        List<String> list = new ArrayList<>();
        list.add(userInfo.getRoles());

        info.setAvatar(userInfo.getAvatar());
        info.setId(userInfo.getId());
        info.setIntroduction(userInfo.getIntroduction());
        info.setName(userInfo.getName());
        info.setRoles(list);
        res.setCode(20000);
        res.setMsg("success");
        res.setData(info);

        return GsonUtils.toJson(res);
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
