package com.fl;

import com.fl.entity.User;
import com.fl.service.UserService;
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
    private UserService userService;


    @Test
    public void test() {

    User user = userService.selectUserInfo(2);

        System.out.println(user);
}

}
