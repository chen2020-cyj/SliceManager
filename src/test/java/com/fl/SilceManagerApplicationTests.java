package com.fl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fl.entity.*;
import com.fl.model.ChildrenInfo;
import com.fl.model.PowerInfo;
import com.fl.model.clientRes.ResData;
import com.fl.service.*;
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
    @Autowired
    private SubtitleTaskService subtitleTaskService;
//    @Autowired
//    pri

    @Test
    public void test() {


//        List<FilmInfo> all = filmInfoService.All();
//
//        for (int i = 0; i < all.size(); i++) {
//            String str = getRandomString(8);
//
//            FilmInfo filmInfo = filmInfoService.selectByFilmId(str);
//
//            if (filmInfo == null){
//                all.get(i).setFilmId(str);
//                all.get(i).setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
//                filmInfoService.updateByFilmInfoId(all.get(i));
//            }else {
//                while (true){
//                    String randomString = getRandomString(8);
//
//                    FilmInfo info = filmInfoService.selectByFilmId(randomString);
//
//                    if (info == null){
//                        all.get(i).setFilmId(str);
//                        all.get(i).setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
//                        filmInfoService.updateByFilmInfoId(all.get(i));
//
//                        break;
//                    }
//                }
//            }
////            all.get(i).setFilmId(str);
//        }
        SubtitleTask subtitleTask = subtitleTaskService.takeOneTask();
        System.out.println(subtitleTask);

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
