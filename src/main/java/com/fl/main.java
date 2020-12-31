package com.fl;

import com.fl.entity.FilmInfo;
import com.fl.entity.MinioInfo;
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

public class main {
    private static String mess;

//    private static AgentUrl hostPort2 = null;

    private static final MediaType json = MediaType.parse("application/json; charset=utf-8");

    public static void main(String[] args) {
        OkHttpClient okHttpClient = new OkHttpClient();
        ReqSliceServer reqSliceServer = new ReqSliceServer();
// request body
        MinioBackMessage minioBackMessage = new MinioBackMessage();
        minioBackMessage.setActualSize("13G");
        minioBackMessage.setOriginalSize("11");
        minioBackMessage.setUrl("www.baidu.com");
        reqSliceServer.setFilmId("ZajQ6OY7Sy");
        reqSliceServer.setCode(6003);
        reqSliceServer.setData(GsonUtils.toJson(minioBackMessage));

        Request request = new Request.Builder().url("http://localhost:8803/deal/taskState")
                .post(RequestBody.create(json, GsonUtils.toJson(reqSliceServer))).build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            ResponseBody body = response.body();
            if (response.isSuccessful()) {
//                log.info("success:{}", body == null ? "" : body.string());
            } else {
//                log.error("error,statusCode={},body={}", response.code(), body == null ? "" : body.string());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//            for (int j=i+i;j<str.length();j++){
//                if (str.charAt(i)){
//
//                }
//            }
//        }
    }

//    public static List<String> select(int k,int[] arr){
//        List<Integer> list = new ArrayList<>();
//        for (int i =0;i<arr.length;i++){
//            list.add(arr[i]);
//        }
//        Integer min = Collections.min(list);
//        System.out.println(min);
//        return null;
//    }



}
