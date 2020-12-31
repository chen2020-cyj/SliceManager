package com.fl.utils;


import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.*;

public class Md5Utils {
    /**
     * Description:MD5工具生成token
     *
     * @param value
     * @return
     */
    public String getMD5Value(String value) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5ValueByteArray = messageDigest.digest(value.getBytes());
            BigInteger bigInteger = new BigInteger(1, md5ValueByteArray);
            return bigInteger.toString(16).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成签名
     *
     * @param map
     * @return
     */
    public static String getSignToken(Map<String, Object> map) {
        String result = "";
        try {
            List<Map.Entry<String, Object>> infoIds = new ArrayList<Map.Entry<String, Object>>(map.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Map.Entry<String, Object>>() {

                public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });
            // 构造签名键值对的格式
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> item : infoIds) {
                if (item.getKey() != null || item.getKey() != "") {
                    String key = item.getKey();
                    Object val = item.getValue();
                    if (!(val == "" || val == null)) {
                        sb.append(key + "=" + val + "&");
                    }
                }
            }
            result = sb.toString();
            //进行MD5加密
            /*     result = getMD5Value(result);*/
        } catch (Exception e) {
            return null;
        }
        return result;
    }
}
