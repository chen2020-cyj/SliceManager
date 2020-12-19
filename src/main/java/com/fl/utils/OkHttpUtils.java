package com.fl.utils;

import com.fl.model.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
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
        String url = "https://m.douban.com/j/search/?q="+filmName+"&t=movie&p=0";
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

    public static List<DouBanData> douBanSearch(String name){
        String str = OkHttpUtils.getHttp(name);

        DouBan douBan = GsonUtils.fromJson(str, DouBan.class);
        String html = douBan.getHtml();

        Document parse = Jsoup.parse(html);

        Element body = parse.body();

        Elements li = body.select("li");

        List<DouBanData> list = new ArrayList<>();
//        System.out.println(body);
        for (int i = 0; i < li.size(); i++) {
            DouBanData douBanData = new DouBanData();
            Element element = li.get(i);
            Elements a = element.getElementsByTag("a");
            Element element1 = a.get(0);
            String href = element1.attr("href");
            String[] subjects = href.split("subject");
            String replace = subjects[1].replace("/", "");

            douBanData.setDoubanId(Integer.valueOf(replace));

            Elements elementsByClass = element.getElementsByClass("subject-title");
            douBanData.setTitle(elementsByClass.get(0).text());

            list.add(douBanData);
        }
        return list;
    }
}
