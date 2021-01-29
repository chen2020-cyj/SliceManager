package com.fl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.entity.FilmInfo;
import com.fl.entity.Menu;
import com.fl.entity.RoleInfo;
import com.fl.entity.TaskManager;
import com.fl.model.ChildrenInfo;
import com.fl.model.PowerInfo;
import com.fl.model.clientRes.ResData;
import com.fl.service.FilmInfoService;
import com.fl.service.MenuService;
import com.fl.service.RoleInfoService;
import com.fl.service.TaskManagerService;
import com.fl.utils.GsonUtils;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SilceManagerApplication.class)
public class SilceManagerApplicationTests {

    @Resource
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FilmInfoService filmInfoService;
    @Autowired
    private TaskManagerService taskManagerService;
    @Autowired
    private RoleInfoService roleInfoService;
    @Autowired
    private MenuService menuService;



    @Test
    public void test() {

        List<RoleInfo> list = roleInfoService.selectAll();

        List<ChildrenInfo> infos = new ArrayList<>();

        List<RoleInfo> roleList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {

            ChildrenInfo childrenInfo = new ChildrenInfo();
            if (list.get(i).getPid() == 0){
                childrenInfo.setId(list.get(i).getId());
                childrenInfo.setIcon("&#xe63a;");
                childrenInfo.setRoleName(list.get(i).getName());
                childrenInfo.setChildren("");
                childrenInfo.setPower("");
                roleList.add(list.get(i));
                infos.add(childrenInfo);
            }
        }

        for (int i = 0; i < roleList.size(); i++) {
            List<ChildrenInfo> list1 = find(roleList.get(i).getId(), infos, list);
//            System.out.println(GsonUtils.toJson(list1));
            for (int k = 0; k < list1.size(); k++) {
                for (int n = 0; n < list.size(); n++) {
                    if (list1.get(k).getId() == list.get(n).getPid()){
                        List<ChildrenInfo> list2 = find(list1.get(k).getId(), infos, list);
                        list1.get(k).setChildren(list2);
                    }
                }
//                if (list.contains(list1.get(k))){
//                    System.out.println("adadadaadad");
//                    List<ChildrenInfo> list2 = find(list1.get(k).getId(), infos, list);
//                    list1.get(k).setChildren(list2);
//                }
            }
            infos.get(i).setChildren(list1);
        }

        for (int i = 0; i < infos.size(); i++) {
            List<Menu> menuList = menuService.selectByRole(infos.get(i).getId());

            List<PowerInfo> powList = new ArrayList<>();
            for (int k = 0; k < menuList.size(); k++) {
                PowerInfo powerInfo = new PowerInfo();
                if (menuList.get(k).getPid() == 0){
                    powerInfo.setPowerName(menuList.get(k).getTitle());
                    powerInfo.setChildren("");
                    powerInfo.setId(menuList.get(k).getId());

                    powList.add(powerInfo);
                }
            }
            for (int k = 0; k < powList.size(); k++) {
                List<PowerInfo> dd = dd(powList.get(i).getId(), menuList);
                if (dd.size() == 0){
                    powList.get(k).setChildren("");
                }else {
                    powList.get(k).setChildren(dd);
                }
            }
            if (powList.size() == 0){
                infos.get(i).setPower("");
            }else {
                infos.get(i).setPower(powList);
            }

        }

        System.out.println(GsonUtils.toJson(infos));
    }


    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(36);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static List<ChildrenInfo> find(Integer id,List<ChildrenInfo> childList,List<RoleInfo> list){
        List<ChildrenInfo> ss = new ArrayList<>();


        for (int i = 0; i < list.size(); i++) {
            ChildrenInfo info = new ChildrenInfo();
            if (id == list.get(i).getPid()){
                info.setId(list.get(i).getId());
                info.setRoleName(list.get(i).getName());
                info.setPower("");
                info.setIcon("&#xe63a;");
                info.setChildren("");
                ss.add(info);
            }
        }
        return ss;
    }


    public static List<PowerInfo> dd(Integer id,List<Menu> list){
        List<PowerInfo> powerList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            PowerInfo powerInfo = new PowerInfo();
            if (id == list.get(i).getPid()){
                powerInfo.setPowerName(list.get(i).getTitle());
                powerInfo.setId(list.get(i).getId());
                powerInfo.setChildren("");
                powerList.add(powerInfo);
            }
        }
        return powerList;
    }
}
