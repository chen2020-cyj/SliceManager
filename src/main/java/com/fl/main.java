package com.fl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fl.model.Msg;
import com.fl.model.clientRes.ResData;
import com.fl.model.clientRes.ResToken;
import com.fl.utils.GsonUtils;
import net.sf.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class main {

    public static void main(String[] args) {
//        ResData res = new ResData();
//        ResData res2 = new ResData();
//        res.setData("adadad");
//        res.setMsg("比你好");
//        res.setCode(0);
//        List<ResData> list = new ArrayList<>();
//
//        list.add(res);
//
//        res2 = list.get(0);
//        res2.setData("帅帅");
//        System.out.println(res2);
        String str = "784.75MB";
        String mb = str.replace("MB", "");
        System.out.println(mb);
    }


}
