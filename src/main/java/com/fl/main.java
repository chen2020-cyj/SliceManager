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
//        String str = "[{\"resolving\":\"720\",\"url\":\"https://www.cnblogs.com/lovechengyu/p/8032039.html\"},{\"resolving\":\"480\",\"url\":\"https://www.cnblogs.com/lovechengyu/p/8032039.html\"},{\"resolving\":\"320\",\"url\":\"https://www.cnblogs.com/lovechengyu/p/8032039.html\"}]\n";
//
//        Gson gson = new Gson();
//
//        List<UploadUrl> list = gson.fromJson(str, new TypeToken<List<UploadUrl>>() {
//        }.getType());
//
//        System.out.println(list);

        String url = "D:\\uploadImage\\JvbLDPyohM.jpg";
        File file = new File(url);

        System.out.println(file.exists());
        MinioPicUpLoad minioPicUpLoad = new MinioPicUpLoad();
        minioPicUpLoad.push("test-upload","800000001","jpg",url);
    }

}
