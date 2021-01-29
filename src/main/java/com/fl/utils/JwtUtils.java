package com.fl.utils;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.sun.org.apache.xml.internal.security.algorithms.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    //过期时间   * 1000
    public static final long EXPIRE_TIME = 20 * 60 * 100;
    //私钥
    private static final String TOKEN_SECRET = "privateKey";

    /**
     * 生成签名，30分钟过期
     * @param **username**
     * @param **password**
     * @return ,Object permission,String name
     */
    public static String sign(Object userId) {
        try {
            // 设置过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            // 私钥和加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            // 设置头部信息
            Map<String, Object> header = new HashMap<>(2);
            header.put("Type", "Jwt");
            header.put("alg", "HS256");
            // 返回token字符串
            return JWT.create()
                    .withHeader(header)
                    .withClaim("userId", String.valueOf(userId))
//                    .withClaim("auth",String.valueOf(permission))
//                    .withClaim("name",name)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     *
     * @param **token**
     * @return
     */
    public static Integer verify(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            algorithm.getName();
            JWTVerifier verifier = JWT.require(algorithm).build();

            DecodedJWT jwt = verifier.verify(token);
            Integer userId = jwt.getClaim("userId").asInt();

            return userId;
        } catch (Exception e){
//            e.printStackTrace();
            return 0;
        }
    }
    /**
     * 获取token里面的消息
     */
    public static String tokenInfo(String token,String str){
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim(str).asString();
        }catch (Exception e){
            return null;
        }

    }
    /**
     * 用于存储用户的权限
     */
    public static String power(Object permission,String name) {
        try {
            // 设置过期时间
            Date date = new Date(System.currentTimeMillis() + (30 * 60 * 1000000));
            // 私钥和加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            // 设置头部信息
            Map<String, Object> header = new HashMap<>(2);
            header.put("Type", "Jwt");
            header.put("alg", "HS256");
            // 返回token字符串
            return JWT.create()
                    .withHeader(header)
//                    .withClaim("userId", String.valueOf(userId))
                    .withClaim("auth",String.valueOf(permission))
                    .withClaim("name",name)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
