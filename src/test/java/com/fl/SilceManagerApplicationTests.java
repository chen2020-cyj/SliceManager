package com.fl;

import com.fl.entity.FilmInfo;
import com.fl.service.FilmInfoService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SilceManagerApplication.class)
public class SilceManagerApplicationTests {

    @Autowired
    private FilmInfoService filmInfoService;


    @Test
    public void test() {
        List<FilmInfo> filmInfos = filmInfoService.allFilmInfo();
        for (int i = 0; i < filmInfos.size(); i++) {
            filmInfos.get(i).setWhetherUpload("0");
            filmInfos.get(i).setUpdateTime(String.valueOf(System.currentTimeMillis()/1000));
            filmInfoService.updateInfo(filmInfos.get(i));
        }
//        UserTest userTest = new UserTest();
//        userTest.setName("3543543");
//        userTestService.insertDDD(userTest);


    }

}
