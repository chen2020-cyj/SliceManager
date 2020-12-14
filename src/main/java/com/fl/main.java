package com.fl;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fl.entity.FilmSourceRecord;
import com.fl.model.*;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResToken;

import com.fl.service.FilmSourceService;
import com.fl.utils.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.org.apache.bcel.internal.generic.NEW;
import lombok.Data;
import net.sf.json.JSONArray;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class main {
    private static String mess;

    public static void main(String[] args) {


//        String url = "D:\\uploadImage\\shuaishuai.jpg";
////        System.out.println(url.indexOf("\\"));
//        String afkjafkahfak = FileUtils.fixFileName(url, "afkjafkahfak");
//        System.out.println(afkjafkahfak);
////
////        System.out.println(file.exists());
////        MinioPicUpLoad minioPicUpLoad = new MinioPicUpLoad();
////        minioPicUpLoad.push("test-upload","800000001","jpg",url);

        test test = new test();
        Map<String,String> map = new HashMap<>();
        map.put("haha","heheh");
        map.put("dada","heddddheh");
        test.setMap(map);
        test.setMess("cucucuucu");

        System.out.println(GsonUtils.toJson(test));
//      data.map
    }


    @Data
    static
    class test{
        String mess;
        Map<String,String> map;
    }


}
