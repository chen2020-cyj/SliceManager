package com.fl;

import com.fl.utils.SpringContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableCaching
@EnableTransactionManagement
public class SilceManagerApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(SilceManagerApplication.class, args);
            System.setProperty("spring.devtools.restart.enabled", "false");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Bean
    public SpringContextHolder springContextHolder(){
        return new SpringContextHolder();
    }
}


