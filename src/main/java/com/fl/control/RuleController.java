package com.fl.control;


import com.fl.entity.Menu;
import com.fl.entity.Rule;
import com.fl.entity.User;
import com.fl.model.clientReq.TestUserInfo;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientReq.Roles;
import com.fl.model.clientRes.ResMenu;
import com.fl.service.MenuService;
import com.fl.service.RuleUserInfoService;
import com.fl.service.UserService;
import com.fl.utils.GsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
    @Autowired
    MenuService menuService;

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
    @ApiOperation("查询权限节点")
    @PostMapping("/getMenu")
    public ResData getMenu(@RequestBody TestUserInfo testUserInfo){

        List<Menu> menuList = menuService.selectAll();
        List<ResMenu> list = new ArrayList<>();
        System.out.println(menuList);
        for (int i = 0; i < menuList.size(); i++) {
            ResMenu resMenu = new ResMenu();
            if (menuList.get(i).getWhetherMenu() == 1){
                resMenu.setMainMenu(menuList.get(i));
                list.add(resMenu);
            }
        }
        for (int i = 0; i < list.size(); i++) {

            List<Menu> menus = menuService.selectByPid(list.get(i).getMainMenu().getId());
            if (menus.size() == 0){

            }else {
                list.get(i).setChildrenMenu(menus);
            }


        }

        res.setCode(0);
        res.setMsg("success");
        res.setData(list);

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
