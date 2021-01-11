package com.fl;

import com.auth0.jwt.interfaces.Claim;
import com.fl.entity.FilmInfo;
import com.fl.entity.MinioInfo;
import com.fl.model.*;

import com.fl.model.clientRes.ReqSliceServer;
import com.fl.model.clientRes.ResData;
import com.fl.model.sliceServerReq.MinioBackMessage;
import com.fl.utils.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import lombok.Data;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class main {
    private static String mess;
    private static JwtBuilder jwtBuilder;
    private static JwtParser jwtParser;

    public static void main(String[] args) {

        List<String> dd = new ArrayList<>();
        dd.add("user:list");
        dd.add("user:menu");
//        a == true ? 1 : 2;
        String sign = JwtUtils.sign(1,dd,"88888888");

        Integer verify = JwtUtils.verify(sign);
        System.out.println(verify);

        String name = JwtUtils.tokenInfo(sign, "userId");
        System.out.println(name);

        String userId = JwtUtils.tokenInfo(sign, "auth");

         Collection<? extends GrantedAuthority> authorities = !StringUtils.isEmpty(userId)?
                 Arrays.stream(userId.toString().replace("[","").replace("]","").split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList()):Collections.emptyList();

        System.out.println(authorities);

        MinioMsg msg = new MinioMsg();
        MinioMsg msg2 = new MinioMsg();
        List<MinioMsg> list = new ArrayList<>();

        msg.setId(1);
        msg.setResolvingPower("720");
        msg2.setId(2);
        msg2.setResolvingPower("480");
        list.add(msg);
        list.add(msg2);

        System.out.println(GsonUtils.toJson(list));
        String ffdd = "[{\"id\":1,\"resolvingPower\":\"720\"},{\"id\":2,\"resolvingPower\":\"480\"},{\"id\":3,\"resolvingPower\":\"320\"}]";
    }

}
@Data
class MinioMsg{
    private Integer id;

    private String resolvingPower;
}
