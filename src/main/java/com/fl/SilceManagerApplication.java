package com.fl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SilceManagerApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(SilceManagerApplication.class, args);
            System.setProperty("spring.devtools.restart.enabled", "false");
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}


