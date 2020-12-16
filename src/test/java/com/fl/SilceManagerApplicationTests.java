package com.fl;

import com.fl.entity.User;
import com.fl.model.UploadUrl;
import com.fl.service.FilmInfoService;
import com.fl.service.UserService;
import com.fl.utils.GsonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jdk.nashorn.internal.parser.TokenType;
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

        String str = "[{\"resolving\":\"720\",\"url\":\"http://162.245.236.170/film/ZsGHknElOh/ZsGHknElOh-32001.m3u8\"}]";

        Gson gson = new Gson();
        List<UploadUrl> list = gson.fromJson(str, new TypeToken<List<UploadUrl>>() {
        }.getType());
        System.out.println(list);


    }

}
