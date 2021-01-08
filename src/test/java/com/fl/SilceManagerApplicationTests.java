package com.fl;

import com.fl.entity.FilmInfo;
import com.fl.entity.TaskManager;
import com.fl.service.FilmInfoService;
import com.fl.service.TaskManagerService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SilceManagerApplication.class)
public class SilceManagerApplicationTests {

    @Resource
    private PasswordEncoder passwordEncoder;
    @Autowired
    private FilmInfoService filmInfoService;

    @Autowired
    private TaskManagerService taskManagerService;
    @Test
    public void test() {
        System.out.println(passwordEncoder.encode("123456"));

    }

}
