package com.fl;
import com.fl.Agent.AgentUrl;
import com.fl.entity.FilmInfo;
import com.fl.model.*;

import com.fl.model.clientRes.ReqSliceServer;
import com.fl.model.clientRes.ResData;
import com.fl.model.sliceServerReq.MinioBackMessage;
import com.fl.utils.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

import static com.fl.Agent.Agent.getHostPort;
public class main {
    private static String mess;

    private static AgentUrl hostPort2 = null;

    private static final MediaType json = MediaType.parse("application/json; charset=utf-8");

    public static void main(String[] args) {

//        MinioBackMessage minioBackMessage = new MinioBackMessage();
//        minioBackMessage.setUrl("http://162.245.236.170/film/ZsGHknElOh/ZsGHknElOh-32001.m3u8");
//        minioBackMessage.setOriginalSize("10.0");
//        minioBackMessage.setActualSize("2.79G");
//
//
////        "{\"segmentUploadComplete\":\"\",\"segmentUpload\":\"6003,6004\",\"segmentUploadFail\":\"\"}"
//        ReqSliceServer resData = new ReqSliceServer();
//        resData.setCode(6004);
//        resData.setFilmId("SBHKTSilHW");
//        resData.setData(minioBackMessage);
////        System.out.println(GsonUtils.toJson(resData));
////        MinioBackMessage minioBackMessage1 = GsonUtils.fromJson(GsonUtils.toJson(resData.getData()), MinioBackMessage.class);
////        System.out.println(minioBackMessage1);
//
//        OkHttpClient client = new OkHttpClient();//创建OkHttpClient对象。
////        FormBody.Builder formBody = new FormBody.Builder();//创建表单请求体
//
//        RequestBody formBody;
//        formBody = RequestBody.create(json,GsonUtils.toJson(resData));
//        System.out.println(GsonUtils.toJson(resData));
//        Request request = new Request.Builder()//创建Request 对象。
//                .url("http://localhost:8803/deal/taskState")
//                .post(formBody)//传递请求体
//                .build();
////        {"segmentStart":"2001","segmentSuccess":"","segmentFail":""}
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(@NotNull Call call, @NotNull IOException e) {
//
//            }
////            {"segmentStart":"2001","segmentSuccess":"2011","segmentFail":""}
//            @Override
//            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
//                System.out.println(response.body().string());
//            }
//        });
//        String str = "[{"\0":456,1:654}]";

//        Gson gson = new Gson();
//
//        List<String> list = gson.fromJson(str, new TypeToken<List<String>>() {
//        }.getType());
//
//        System.out.println(list);
//        List<Integer> list = new ArrayList<>();
//        list.add(1);
//        list.add(2);
//        list.add(3);
//        list.add(4);
//        list.add(5);
//
//        System.out.println(list.subList(5,10));
    }





}
