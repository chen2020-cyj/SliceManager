package com.fl;

import com.fl.entity.RoleInfo;
import com.fl.model.*;

import com.fl.utils.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;

import java.io.IOException;
import java.util.*;

public class main {
    private static String mess;
    private static JwtBuilder jwtBuilder;
    private static JwtParser jwtParser;
    static String msg = "";
    public static void main(String[] args) throws IOException {

//        List<String> dd = new ArrayList<>();
//        dd.add("user:list");
//        dd.add("user:menu");
////        a == true ? 1 : 2;
//        String sign = JwtUtils.sign(1,dd,"88888888");
//
//        Integer verify = JwtUtils.verify(sign);
//        System.out.println(verify);
//
//        String name = JwtUtils.tokenInfo(sign, "userId");
//        System.out.println(name);
//
//        String userId = JwtUtils.tokenInfo(sign, "auth");
//
//         Collection<? extends GrantedAuthority> authorities = !StringUtils.isEmpty(userId)?
//                 Arrays.stream(userId.toString().replace("[","").replace("]","").split(","))
//                .map(SimpleGrantedAuthority::new)
//                .collect(Collectors.toList()):Collections.emptyList();
//
//        System.out.println(authorities);
//
//        MinioMsg msg = new MinioMsg();
//        MinioMsg msg2 = new MinioMsg();
//        List<MinioMsg> list = new ArrayList<>();
//
//        msg.setId(1);
//        msg.setResolvingPower("720");
//        msg2.setId(2);
//        msg2.setResolvingPower("480");
//        list.add(msg);
//        list.add(msg2);
//
//        System.out.println(GsonUtils.toJson(list));
//        String ffdd = "[{\"id\":1,\"resolvingPower\":\"720\"},{\"id\":2,\"resolvingPower\":\"480\"},{\"id\":3,\"resolvingPower\":\"320\"}]";
//        OkHttpClient httpClient = new OkHttpClient();
//
//        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
//
//        String post = "{\"test\":123}";
//
//
//
//
//        ReqSliceServer resData = new ReqSliceServer();
//
//
//        List<UploadUrl> list = new ArrayList<>();
//        UploadUrl uploadUrl = new UploadUrl();
//        uploadUrl.setUrl("www.baidu.com");
//        uploadUrl.setResolving("720");
//        list.add(uploadUrl);
//
//        MinioBackMessage msg = new MinioBackMessage();
//        msg.setUrl("www.baidu.com");
//        msg.setOriginalSize("3.6");
//        msg.setActualSize("5.0G");
//
//        resData.setCode(6004);
//        resData.setData(msg);
//        resData.setFilmId("IRNR2YrPSY");
//
//        RequestBody requestBody = RequestBody.create(mediaType, GsonUtils.toJson(resData));
////        http://localhost:8803/deal/swagger-ui.html#/%E7%94%A8%E6%88%B7%E7%99%BB%E9%99%86%E6%B3%A8%E5%86%8C%E6%8E%A5%E5%8F%A3/loginUsingPOST
//
//        Request request = new Request.Builder()
//                .post(requestBody)
//                .url("http://localhost:8803/deal/taskState")
//                .build();
//
//        Response response = httpClient.newCall(request).execute();
//
//
//        System.out.println(response);
        // 首先需要创建一个OkHttpClient对象用于Http请求, 可以改成全局型

//        System.out.println(System.currentTimeMillis()/1000);

//        UploadUrl uploadUrl = new UploadUrl();
//        uploadUrl.setUrl("www.baidu.com");
//        uploadUrl.setResolving("720");
//
//        UploadUrl uploadUrl2 = new UploadUrl();
//        uploadUrl2.setUrl("www.baidu.com");
//        uploadUrl2.setResolving("480");
//
//        UploadUrl uploadUrl3 = new UploadUrl();
//        uploadUrl3.setUrl("www.baidu.com");
//        uploadUrl3.setResolving("320");
//
//        List<UploadUrl> list = new ArrayList<>();
//
//        list.add(uploadUrl);
//        list.add(uploadUrl2);
//        list.add(uploadUrl3);
//
//        System.out.println(GsonUtils.toJson(list));
        String str = "修改管理员信息  reqUpdateAdminUser: ReqUpdateAdminUser(userId=12, name=汪汪, password=, roleId=1)  request: SecurityContextHolderAwareRequestWrapper[ org.springframework.security.web.header.HeaderWriterFilter$HeaderWriterRequest@217ed674]";

        String substring = str.substring(str.indexOf("password")+9, str.indexOf("roleId")-2);

        System.out.println(substring.equals(""));


    }



}

