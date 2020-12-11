package com.fl.utils;

import com.fl.model.DouBanResData;
import com.fl.model.DoubanRes;
import com.fl.model.Subject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OkHttpUtils {

    /**
     * 请求接口
     * @param filmName
     * @return
     */
    public static String getHttp(String filmName){
        String url = "http://106.55.173.177:8081/index.php/search?q="+filmName+"&page=0";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()

                .build();
        try {
            Request request = new Request.Builder().get().url(url).build();
            Response response = okHttpClient.newCall(request).execute();

            String html = response.body().string();

            return html;
        }catch (Exception e){
            return "err";
        }

    }

    /**
     * 请求电影详情页面
     * @param doubanId
     * @return
     */
    public static String requestHtml(String doubanId){
        String url = "https://movie.douban.com/subject/"+doubanId+"/";
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()

                .build();
        try {
            Request request = new Request.Builder().get().url(url).build();
            Response response = okHttpClient.newCall(request).execute();

            String html = response.body().string();

            return html;
        }catch (Exception e){
            return "err";
        }

    }

    /**
     * 获取相关电影，获得精确的电影信息
     */
    public static List<Subject> getFilm(String html){


        String http = getHttp(html);
        DoubanRes doubanRes = GsonUtils.fromJson(http, DoubanRes.class);

        DouBanResData douBanResData = GsonUtils.fromJson(GsonUtils.toJson(doubanRes.getData()), DouBanResData.class);

        try {
            Gson gson = new Gson();
            List<Subject> subject = gson.fromJson(GsonUtils.toJson(douBanResData.getSubject()), new TypeToken<List<Subject>>(){}.getType());
            return subject;
        }catch (Exception e){
          return null;
        }

    }
}
