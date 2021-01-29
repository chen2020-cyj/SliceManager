package com.fl.control;


import com.fl.aop.annotation.Log;
import com.fl.entity.*;
import com.fl.model.Authority;
import com.fl.model.ChildrenInfo;
import com.fl.model.PowerInfo;
import com.fl.model.UserInfo;
import com.fl.model.clientReq.*;

import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResGetInfo;
import com.fl.model.clientRes.ResRolePower;
import com.fl.model.clientRes.ResUserPowerInfo;
import com.fl.service.*;
import com.fl.utils.GsonUtils;
import com.fl.utils.HAUtils;
import com.fl.utils.JwtUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "权限管理接口")
@RestController
public class RuleController {

    @Autowired
    UserService userService;
    @Autowired
    MenuService menuService;
    @Autowired
    RoleInfoService roleInfoService;
    @Autowired
    SysRoleRefRouteService sysRoleRefRouteService;
    @Resource
    private PasswordEncoder passwordEncoder;

    private static Map<String,String> map = new HashMap<>();
    private static ResData res = new ResData();
    @PostMapping("/getInfo")
    public ResData getInfo(HttpServletRequest request){
        ResData resData = new ResData();
        String token = request.getHeader("token");
        String userId = JwtUtils.tokenInfo(token, "userId");

        User user = userService.selectByUserId(Integer.valueOf(userId));

        List<SysRoleRefRoute> list = sysRoleRefRouteService.selectByRoleId(user.getRoleId());

        List<Integer> menuIdList= new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            menuIdList.add(list.get(i).getMenuId());
        }
        ResGetInfo getInfo = new ResGetInfo();

        getInfo.setId(user.getId());
        getInfo.setName(user.getName());
        getInfo.setMenuId(menuIdList);

        resData.setCode(0);
        resData.setMsg("success");
        resData.setData(getInfo);

        return resData;
    }
    @ApiOperation("用户所拥有的权限节点")
    @PostMapping("/getMenu")
    public ResData getMenu(HttpServletRequest request){

        String token = request.getHeader("token");
        String userId = JwtUtils.tokenInfo(token, "userId");

        User user = userService.selectUserInfo(Integer.valueOf(userId));

        //查询出用户所拥有的权限
        List<Menu> menuList = menuService.selectByRoleId(user);

//        System.out.println(menuList);

        List<Authority> mainList = new ArrayList<>();
        for (int i = 0; i < menuList.size(); i++) {
            if (menuList.get(i).getPid() == 0){
                Authority authority = new Authority();
                authority.setId(menuList.get(i).getId());
                authority.setPath(menuList.get(i).getPath());
                authority.setName(menuList.get(i).getTitle());
                authority.setIcon(menuList.get(i).getIcon());
                authority.setViewUrl(menuList.get(i).getViewUrl());
                authority.setChildren("");
//                if (){
//
//                }
                mainList.add(authority);
            }
        }
        for (int i = 0; i < mainList.size(); i++) {
            List<Authority> authorityList = new ArrayList<>();
            for (int j = 0; j < menuList.size(); j++) {
                if (mainList.get(i).getId() == menuList.get(j).getPid()){
                    Authority authority = new Authority();
                    authority.setId(menuList.get(j).getId());
                    authority.setName(menuList.get(j).getTitle());
                    authority.setPath(menuList.get(j).getPath());
                    authority.setIcon(menuList.get(j).getIcon());
                    authority.setViewUrl(menuList.get(j).getViewUrl());
                    authority.setChildren("");
                    authorityList.add(authority);
                }
            }
            if (authorityList.size() == 0){
                mainList.get(i).setChildren("");
            }else {
                mainList.get(i).setChildren(authorityList);
            }

        }
        for (int i = 0; i < mainList.size(); i++) {
            List<Authority> objList;
            if (mainList.get(i).getChildren().equals("")){
                objList = new ArrayList<>();
            }else {
                objList = HAUtils.objToList(mainList.get(i).getChildren(), Authority.class);
            }
            for (int j = 0; j < objList.size(); j++) {
                List<Authority> list = new ArrayList<>();
                for (int k = 0; k < menuList.size(); k++) {
                    if (objList.get(j).getId() == menuList.get(k).getPid()){
//                            System.out.println("objId是+++"+objList.get(j).getId()+"子id是::::"+menuList.get(k).getPid());
                        Authority authority = new Authority();
                        authority.setIcon(menuList.get(k).getIcon());
                        authority.setId(menuList.get(k).getId());
                        authority.setName(menuList.get(k).getTitle());
                        authority.setPath(menuList.get(k).getPath());
                        authority.setViewUrl(menuList.get(k).getViewUrl());
                        authority.setChildren("");
                        list.add(authority);
                    }
                }
                if (list.size() == 0){
                    objList.get(j).setChildren("");
                }else {
                    objList.get(j).setChildren(list);
                }

            }
        }
        res.setCode(0);
        res.setMsg("success");
        res.setData(mainList);

        return res;
    }

    @ApiOperation("查询所管理的角色组")
    @PostMapping(value = "/selectUserGetInfo", produces = "application/json;charset=UTF-8")
    public ResData selectUserGetInfo(@RequestBody ReqSelectUserGetInfo reqSelectUserGetInfo) {
        ResData resData = new ResData();
        User user = userService.selectByUserId(reqSelectUserGetInfo.getUserId());
        List<RoleInfo> roleInfos = roleInfoService.selectAll();
        //判断是否是超级管理员
        if (user.getUsername().equals("admin")){


            resData.setCode(0);
            resData.setMsg("success");
            resData.setData(roleInfos);
            return resData;
        }else {
            RoleInfo roleInfo = roleInfoService.selectById(Integer.valueOf(user.getRoleId()));

            List<RoleInfo> list = new ArrayList<>();

            for (int i = 0; i < roleInfos.size(); i++) {
                if (roleInfos.get(i).getId() == roleInfo.getId()){
                    RoleInfo info = new RoleInfo();
                    info.setName(roleInfos.get(i).getName());
                    info.setPid(roleInfos.get(i).getPid());
//                    info.setMenuId("");
                    info.setId(roleInfos.get(i).getId());
                    list.add(roleInfo);
                    findRoleInfo(roleInfos,roleInfos.get(i).getId(),list);
                }
            }

//            findRoleInfo(roleInfos,roleInfo.getId(),list);

            System.out.println(list);
            resData.setCode(0);
            resData.setMsg("success");
            if (list.size() == 0 ){
                resData.setData("");
            }else {
                resData.setData(list);
            }
            return resData;
        }
    }
    public void findRoleInfo(List<RoleInfo> list,Integer id,List<RoleInfo> newRoleList){

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPid() == id){
                RoleInfo roleInfo = new RoleInfo();
                roleInfo.setName(list.get(i).getName());
                roleInfo.setPid(list.get(i).getPid());
//                roleInfo.setMenuId("");
                roleInfo.setId(list.get(i).getId());
                findRoleInfo(list,list.get(i).getId(),newRoleList);
                newRoleList.add(roleInfo);
            }
        }
    }
    @PreAuthorize("@zz.check('admin:roleManager')")
    @ApiOperation("角色组管理")
    @PostMapping(value = "/roleManager", produces = "application/json;charset=UTF-8")
    public ResData roleManager(HttpServletRequest request){
        ResData resData = new ResData();
        String token = request.getHeader("token");

        String userId = JwtUtils.tokenInfo(token, "userId");

        User user = userService.selectByUserId(Integer.valueOf(userId));
//        System.out.println(user);
        ResUserPowerInfo info = new ResUserPowerInfo();

        List<Menu> menuList = menuService.selectAll();

        List<RoleInfo> list = roleInfoService.selectAll();
        if (user.getUsername().equals("admin")){
            ChildrenInfo childInfo = new ChildrenInfo();
            List<ChildrenInfo> infoList = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                int num = 0;
                List<PowerInfo> powList = new ArrayList<>();

                if (list.get(i).getId() == user.getRoleId()){
                    List<Menu> list1 = menuService.selectByRole(list.get(i).getId());

                    for (int j = 0; j < list1.size(); j++) {
                        PowerInfo powerInfo = new PowerInfo();
                        if (list1.get(j).getPid() == 0) {
                            powerInfo.setId(list1.get(j).getId());
                            powerInfo.setPowerName(list1.get(j).getTitle());
                            List<PowerInfo> morePower = findMorePower(list1.get(j).getId(), list1);

                            if (morePower.size() == 0) {
                                powerInfo.setChildren("");
                            } else {
                                powerInfo.setChildren(morePower);
                            }

                            powList.add(powerInfo);
                        }
                    }

                    childInfo.setRoleName(list.get(i).getName());
                    if (powList.size() == 0){
                        childInfo.setPower("");
                    }else {
                        childInfo.setPower(powList);
                    }


                    String str = "";
                    for (int j = 0; j < num; j++) {

                        if (str.equals("")){
                            str ="-";
                        }else {
                            str = str +" "+"-";
                        }
                    }
                    childInfo.setIcon(str);
                    childInfo.setId(list.get(i).getId());
                    childInfo.setChildren("");
                    infoList.add(childInfo);

                    findMoreRole(list.get(i).getId(), list, infoList,num);
                }
            }

//            List<Menu> list2 = menuService.selectByRole(user.getRoleId());

            List<ChildrenInfo> list1 = userAdmin(list);
            info.setChildren(list1);
            info.setSelfInfo(childInfo);
        }else {

            ChildrenInfo childrenInfo = new ChildrenInfo();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId() == user.getRoleId()){
                    childrenInfo.setId(list.get(i).getId());
                    childrenInfo.setRoleName(list.get(i).getName());
                    childrenInfo.setIcon("");
                    childrenInfo.setChildren("");

                    List<Menu> menuList1 = menuService.selectByRole(user.getRoleId());

                    List<PowerInfo> powList= new ArrayList<>();
                    for (int j = 0; j < menuList1.size(); j++) {
                        PowerInfo powerInfo = new PowerInfo();
                        if (menuList1.get(j).getPid() == 0){
                            powerInfo.setId(menuList1.get(j).getId());
                            powerInfo.setPowerName(menuList1.get(j).getTitle());

                            List<PowerInfo> morePower = findMorePower(menuList1.get(j).getId(), menuList1);

                            if (morePower.size() == 0){
                                powerInfo.setChildren("");
                            }else {
                                powerInfo.setChildren(morePower);
                            }
                            powList.add(powerInfo);
                        }
                    }
                    if (powList.size() == 0){
                        childrenInfo.setPower("");
                    }else {
                        childrenInfo.setPower(powList);
                    }
                    childrenInfo.setPower(powList);
                }
            }
            List<ChildrenInfo> list1 = userOther(user.getRoleId(), list);
            for (int i = 0; i < list1.size(); i++) {
                if (list1.get(i).getRoleName() == childrenInfo.getRoleName()){
                    list1.remove(i);
                }
            }
            System.out.println(GsonUtils.toJson(list1));
            info.setChildren(list1);
            info.setSelfInfo(childrenInfo);
        }
        resData.setCode(0);
        resData.setMsg("success");
        resData.setData(info);

        return resData;
    }

    /**
     * 查询所有的角色 以及子角色
     * @param id
     * @param list
     * @param infoList
     * @param num
     */
    public void findMoreRole(Integer id,List<RoleInfo> list,List<ChildrenInfo> infoList,int num) {
        num = num +1;
        for (int i = 0; i < list.size(); i++) {
            if (id == list.get(i).getPid()) {
//                System.out.println(list.get(i));
                ChildrenInfo childrenInfo = new ChildrenInfo();
                childrenInfo.setRoleName(list.get(i).getName());

                    List<Menu> menuList = menuService.selectByRole(list.get(i).getId());
                    List<PowerInfo> powList = new ArrayList<>();
                    for (int j = 0; j < menuList.size(); j++) {
                    if (menuList.get(j).getPid() == 0){
                        PowerInfo powerInfo = new PowerInfo();

                        powerInfo.setId(menuList.get(j).getId());
                        powerInfo.setPowerName(menuList.get(j).getTitle());
                        List<PowerInfo> morePower = findMorePower(menuList.get(j).getId(), menuList);

                        if (morePower.size() == 0){
                            powerInfo.setChildren("");
                        }else {
                            powerInfo.setChildren(morePower);
                        }
                        powList.add(powerInfo);

                    }
                }

                childrenInfo.setPower(powList);
                String str = "";
                for (int j = 0; j < num; j++) {

                    if (str.equals("")){
                        str ="-";
                    }else {
                        str = str +" "+"-";
                    }
                }
                childrenInfo.setIcon(str);
                childrenInfo.setId(list.get(i).getId());
                childrenInfo.setChildren("");
//                childrenInfo.setPower();
                infoList.add(childrenInfo);

                findMoreRole(list.get(i).getId(), list, infoList,num);

            }
        }
    }

    /**
     * 查询所有权限节点 以及子节点
     * @param id
     * @param menuList
     * @return
     */
    private List<PowerInfo> findMorePower(Integer id,List<Menu> menuList){
        List<PowerInfo> list = new ArrayList<>();

        for (int i = 0; i < menuList.size(); i++) {
            PowerInfo powerInfo = new PowerInfo();
            if (id == menuList.get(i).getPid()){
                powerInfo.setId(menuList.get(i).getId());
                powerInfo.setPowerName(menuList.get(i).getTitle());
                List<PowerInfo> morePower = findMorePower(menuList.get(i).getId(), menuList);
                if (morePower.size() == 0){
                    powerInfo.setChildren("");
                }else {
                    powerInfo.setChildren(morePower);
                }

                list.add(powerInfo);
            }
        }

        return list;
    }

    /**
     * 如果是超级管理员
     * @return
     */
    public List<ChildrenInfo> userAdmin(List<RoleInfo> list) {
        List<ChildrenInfo> childList = new ArrayList<>();


        int num = 0;
        for (int i = 0; i < list.size(); i++) {
            ChildrenInfo childrenInfo = new ChildrenInfo();
            List<PowerInfo> powList = new ArrayList<>();
            if (list.get(i).getPid() == 0) {
                List<Menu> list1 = menuService.selectByRole(list.get(i).getId());
//                System.out.println(GsonUtils.toJson(list1));

                for (int j = 0; j < list1.size(); j++) {
                    PowerInfo powerInfo = new PowerInfo();
                    if (list1.get(j).getPid() == 0) {
                        powerInfo.setId(list1.get(j).getId());
                        powerInfo.setPowerName(list1.get(j).getTitle());
                        List<PowerInfo> morePower = findMorePower(list1.get(j).getId(), list1);

                        if (morePower.size() == 0) {
                            powerInfo.setChildren("");
                        } else {
                            powerInfo.setChildren(morePower);
                        }

                        powList.add(powerInfo);
                    }
                }
                childrenInfo.setRoleName(list.get(i).getName());
                childrenInfo.setPower(powList);
                childrenInfo.setIcon("");
                childrenInfo.setId(list.get(i).getId());
                childrenInfo.setChildren("");
                childList.add(childrenInfo);
                findMoreRole(list.get(i).getId(), list, childList, num);
            }
        }
        return childList;
//        System.out.println(GsonUtils.toJson(childList));
    }

    /**
     * 如果不是超级管理员
     * @return
     */
    public List<ChildrenInfo> userOther(Integer roleId,List<RoleInfo> list){

        List<ChildrenInfo> childList = new ArrayList<>();
        int num = 0;
//        RoleInfo roleInfo = roleInfoService.selectById(roleId);
//
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).getId() == roleId) {
                ChildrenInfo childrenInfo = new ChildrenInfo();

                List<Menu> menuList = menuService.selectByRole(roleId);
                List<PowerInfo> powList = new ArrayList<>();
                for (int j = 0; j < menuList.size(); j++) {
                    if (menuList.get(j).getPid() == 0){
                        PowerInfo powerInfo = new PowerInfo();

                        powerInfo.setId(menuList.get(j).getId());
                        powerInfo.setPowerName(menuList.get(j).getTitle());
                        List<PowerInfo> morePower = findMorePower(menuList.get(j).getId(), menuList);

                        if (morePower.size() == 0) {
                            powerInfo.setChildren("");
                        } else {
                            powerInfo.setChildren(morePower);
                        }

                        powList.add(powerInfo);
                    }
                }

                childrenInfo.setId(list.get(i).getId());
                childrenInfo.setIcon("");
                childrenInfo.setPower(powList);
                childrenInfo.setRoleName(list.get(i).getName());
                childList.add(childrenInfo);
                findMoreRole(list.get(i).getId(), list, childList, num);
            }
        return childList;
    }
    @ApiOperation("查询角色权限")
    @PostMapping(value = "/selectRolePower", produces = "application/json;charset=UTF-8")
    public ResData selectRolePower(@RequestBody ReqSelectRolePower reqSelectRolePower){
        ResData resData = new ResData();

        ResRolePower resRolePower = new ResRolePower();

        RoleInfo roleSelfInfo = roleInfoService.selectById(reqSelectRolePower.getRoleId());

        RoleInfo roleInfo = roleInfoService.selectById(roleSelfInfo.getPid());

        //父级权限
        List<PowerInfo> powParentList = new ArrayList<>();
        if (roleInfo != null){

            List<Menu> parentList = menuService.selectByRole(roleInfo.getId());

            for (int i = 0; i < parentList.size(); i++) {
                PowerInfo powerInfo = new PowerInfo();
                if (parentList.get(i).getPid() == 0){
                    powerInfo.setId(parentList.get(i).getId());
                    powerInfo.setPowerName(parentList.get(i).getTitle());

                    List<PowerInfo> morePower = findMorePower(parentList.get(i).getId(), parentList);
                    powerInfo.setChildren(morePower);
                    powParentList.add(powerInfo);
                }
            }
        }
        if (powParentList.size() == 0){
            resRolePower.setParent("");
        }else {
            resRolePower.setParent(powParentList);
        }

        //自己的权限
        List<Menu> selfList = menuService.selectByRole(reqSelectRolePower.getRoleId());

        List<PowerInfo> powSelfList = new ArrayList<>();

        for (int i = 0; i < selfList.size(); i++) {
            PowerInfo powerInfo = new PowerInfo();
            if (selfList.get(i).getPid() == 0){
                powerInfo.setId(selfList.get(i).getId());
                powerInfo.setPowerName(selfList.get(i).getTitle());

                List<PowerInfo> morePower = findMorePower(selfList.get(i).getId(), selfList);
                powerInfo.setChildren(morePower);
                powSelfList.add(powerInfo);
            }
        }

        resRolePower.setSelf(powSelfList);



        resData.setCode(0);
        resData.setMsg("success");
        resData.setData(resRolePower);

        return resData;
    }
    @PreAuthorize("@zz.check('admin:addRoleInfo')")
    @Log("admin:addRoleInfo")
    @ApiOperation("添加角色组")
    @PostMapping(value = "/addRoleInfo", produces = "application/json;charset=UTF-8")
    public ResData addRoleInfo(@RequestBody ReqAddRoleInfo reqAddRoleInfo){
        ResData resData = new ResData();

        List<SysRoleRefRoute> list = new ArrayList<>();

        RoleInfo roleInfo = new RoleInfo();

        roleInfo.setCreateTime(String.valueOf(System.currentTimeMillis()/1000));
//        roleInfo.setMenuId("");
        roleInfo.setName(reqAddRoleInfo.getRoleName());
        roleInfo.setPid(reqAddRoleInfo.getPid());
        roleInfoService.save(roleInfo);

        String[] split = reqAddRoleInfo.getGroupId().split(",");
        for (int i = 0; i < split.length; i++) {
            SysRoleRefRoute sysRoleRefRoute = new SysRoleRefRoute();

            sysRoleRefRoute.setRoleInfoId(roleInfo.getId());
            sysRoleRefRoute.setMenuId(Integer.valueOf(split[i]));

            list.add(sysRoleRefRoute);
        }

        sysRoleRefRouteService.saveBatch(list);

        resData.setCode(0);
        resData.setMsg("success");
        resData.setData("");

        return resData;
    }
    @PreAuthorize("@zz.check('admin:updateRolePower')")
    @Log("admin:updateRolePower")
    @ApiOperation("修改角色权限")
    @PostMapping(value = "/updateRolePower", produces = "application/json;charset=UTF-8")
    public ResData updateRolePower(@RequestBody ReqUpdateRolePower reqUpdateRolePower){
        ResData resData = new ResData();

        String[] split = reqUpdateRolePower.getGroupId().split(",");

        List<SysRoleRefRoute> list = sysRoleRefRouteService.selectByRoleId(reqUpdateRolePower.getRoleId());

        RoleInfo roleInfo = roleInfoService.selectById(reqUpdateRolePower.getRoleId());
        roleInfo.setName(reqUpdateRolePower.getRoleName());

        List<RoleInfo> roleList = new ArrayList<>();

        roleList.add(roleInfo);
        roleInfoService.updateBatchById(roleList);

        List<Integer> sysRoleList = new ArrayList<>();

        List<Integer> intList = new ArrayList<>();
        List<Integer> intList2 = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            intList.add(Integer.valueOf(split[i]));
            intList2.add(Integer.valueOf(split[i]));
        }
        for (int i = 0; i < list.size(); i++) {
            sysRoleList.add(list.get(i).getMenuId());
        }
        System.out.println(intList);
        intList.removeAll(sysRoleList);


        if (intList.size() == 0){
            sysRoleList.removeAll(intList2);

            if (sysRoleList.size() == 0){

            }else {
                List<Integer> delList = new ArrayList<>();

                for (int i = 0; i < sysRoleList.size(); i++) {
                    for (int j = 0; j < list.size(); j++) {
                        if (sysRoleList.get(i) == list.get(j).getMenuId()){

                            delList.add(list.get(j).getId());
                        }
                    }
                }
//                System.out.println(delList);
                sysRoleRefRouteService.removeByIds(delList);
            }

        }else {
            List<SysRoleRefRoute> sysList = new ArrayList<>();

            for (int i = 0; i < intList.size(); i++) {
                SysRoleRefRoute sysRoleRefRoute = new SysRoleRefRoute();
                sysRoleRefRoute.setRoleInfoId(reqUpdateRolePower.getRoleId());
                sysRoleRefRoute.setMenuId(intList.get(i));

                sysList.add(sysRoleRefRoute);
            }
            sysRoleRefRouteService.saveBatch(sysList);

            sysRoleList.removeAll(intList2);

            if (sysRoleList.size() == 0){

            }else {
                List<Integer> delList = new ArrayList<>();

                for (int i = 0; i < sysRoleList.size(); i++) {
                    for (int j = 0; j < list.size(); j++) {
                        if (sysRoleList.get(i) == list.get(j).getMenuId()){

                            delList.add(list.get(j).getId());
                        }
                    }
                }
                sysRoleRefRouteService.removeByIds(delList);
            }

        }
        resData.setCode(0);
        resData.setMsg("success");
        resData.setData("");

        return resData;
    }
    @PreAuthorize("@zz.check('admin:delRole')")
    @Log("admin:delRole")
    @ApiOperation("删除角色组")
    @PostMapping(value = "/delRole", produces = "application/json;charset=UTF-8")
    public ResData delRole(@RequestBody ReqDelRole reqDelRole) {
        ResData resData = new ResData();

//        RoleInfo roleInfo = roleInfoService.selectById(reqDelRole.getRoleId());
        //删除角色组
        roleInfoService.delRoleById(reqDelRole.getRoleId());

        List<SysRoleRefRoute> list = sysRoleRefRouteService.selectByRoleId(reqDelRole.getRoleId());

        List<Integer> intList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            intList.add(list.get(i).getId());
        }
        //删除角色组中的所有权限
        sysRoleRefRouteService.removeByIds(intList);

        resData.setCode(0);
        resData.setMsg("success");
        resData.setData("");

        return resData;
    }
    @PreAuthorize("@zz.check('admin:getMoreUser')")
    @Log("admin:getMoreUser")
    @ApiOperation("管理员管理")
    @PostMapping(value = "/getMoreUser", produces = "application/json;charset=UTF-8")
    public ResData getMoreUser(HttpServletRequest request){
        ResData resData = new ResData();
        String token = request.getHeader("token");
        String userId = JwtUtils.tokenInfo(token, "userId");

        User user = userService.selectByUserId(Integer.valueOf(userId));

        RoleInfo roleInfo = roleInfoService.selectById(user.getRoleId());

        List<RoleInfo> list = roleInfoService.selectAll();

        List<User> userList = userService.selectAll();

        List<UserInfo> userInfoList = new ArrayList<>();

        if (user.getUsername().equals("admin")){

            for (int i = 0; i < userList.size(); i++) {
                UserInfo userInfo = new UserInfo();
                userInfo.setUsername(userList.get(i).getUsername());
                userInfo.setId(userList.get(i).getId());
                userInfo.setName(userList.get(i).getName());

                RoleInfo roleInfo1 = roleInfoService.selectById(userList.get(i).getRoleId());
                if (roleInfo1 == null){
                    userInfo.setRoleName("没有角色组");
                }else {
                    userInfo.setRoleName(roleInfo1.getName());
                }

                userInfo.setCreateTime(list.get(i).getCreateTime());
                userInfo.setUpdateTime(list.get(i).getUpdateTime());

                userInfoList.add(userInfo);
            }

            resData.setCode(0);
            resData.setMsg("success");
            resData.setData(userInfoList);
        }else {
//            System.out.println(roleInfo.getId());
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getId() == roleInfo.getId()){
                    for (int j = 0; j < userList.size(); j++) {
                        if (userList.get(j).getId() == user.getId()){
                            UserInfo userInfo = new UserInfo();
                            userInfo.setId(userList.get(j).getId());
                            userInfo.setUsername(userList.get(j).getUsername());
                            userInfo.setName(userList.get(j).getName());
                            RoleInfo roleInfo1 = roleInfoService.selectById(userList.get(j).getRoleId());
                            userInfo.setRoleName(roleInfo1.getName());
                            userInfo.setCreateTime(userList.get(j).getCreateTime());
                            userInfo.setUpdateTime(userList.get(j).getUpdateTime());

//                            System.out.println(userInfo);
//                            userInfoList.add(userInfo);
                            findUser(list,userList,userInfoList,userList.get(j).getRoleId(),userList.get(j).getId());
//                            userInfo.setPassword();
                        }
                    }
                    break;
                }
            }

            resData.setCode(0);
            resData.setMsg("success");
            resData.setData(userInfoList);
        }

        return resData;
    }
    public void findUser(List<RoleInfo> roleList,List<User> userList,List<UserInfo> userInfoList,Integer id,Integer userId){

        for (int i = 0; i < roleList.size(); i++) {
            if (roleList.get(i).getPid() == id){
                for (int j = 0; j < userList.size(); j++) {
                    if (userList.get(j).getRoleId() == roleList.get(i).getId()){
                        UserInfo userInfo = new UserInfo();

                        userInfo.setId(userList.get(j).getId());
                        userInfo.setName(userList.get(j).getName());
                        userInfo.setUsername(userList.get(j).getUsername());
                        RoleInfo roleInfo1 = roleInfoService.selectById(userList.get(j).getRoleId());
                        userInfo.setRoleName(roleInfo1.getName());
                        userInfo.setCreateTime(userList.get(j).getCreateTime());
                        userInfo.setUpdateTime(userList.get(j).getUpdateTime());

                        userInfoList.add(userInfo);
                        System.out.println(userInfo);
                        findUser(roleList,userList,userInfoList,userList.get(j).getRoleId(),userList.get(j).getId());
                    }
                }
            }
        }
    }
    @PreAuthorize("@zz.check('admin:delAdminUser')")
    @Log("admin:delAdminUser")
    @ApiOperation("删除管理员")
    @PostMapping(value = "delAdminUser",produces = "application/json;charset=UTF-8")
    public ResData delAdminUser(@RequestBody ReqDelAdminUser reqDelAdminUser){
        ResData resData = new ResData();

        String[] split = reqDelAdminUser.getUserId().split(",");

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            list.add(Integer.valueOf(split[i]));
        }

        userService.removeByIds(list);

        resData.setCode(0);
        resData.setMsg("success");
        resData.setData("");

        return resData;
    }
    @PreAuthorize("@zz.check('admin:updateAdminUser')")
    @Log("admin:updateAdminUser")
    @ApiOperation("编辑修改管理员信息")
    @PostMapping(value = "updateAdminUser",produces = "application/json;charset=UTF-8")
    public ResData updateAdminUser(@RequestBody ReqUpdateAdminUser reqUpdateAdminUser,HttpServletRequest request){
        ResData resData = new ResData();

//        User user = userService.selectByUserId(reqUpdateAdminUser.getUserId());
//        String token = request.getHeader("token");
//        String userId = JwtUtils.tokenInfo(token, "userId");
        User user = userService.selectByUserId(reqUpdateAdminUser.getUserId());
        String encode = "";
        if(reqUpdateAdminUser.getPassword().equals("")){
//            encode = passwordEncoder.encode(user.getPassword());;
            user.setName(reqUpdateAdminUser.getName());
            user.setRoleId(reqUpdateAdminUser.getRoleId());
            user.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
        }else {
            encode = passwordEncoder.encode(reqUpdateAdminUser.getPassword());;
            user.setPassword(encode);
            user.setName(reqUpdateAdminUser.getName());
            user.setRoleId(reqUpdateAdminUser.getRoleId());
            user.setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
        }



        userService.updateToken(user);

        resData.setCode(0);
        resData.setMsg("success");
        resData.setData("");

        return resData;
    }
}
