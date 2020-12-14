package com.fl.utils;

import io.minio.MinioClient;
import io.minio.errors.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinioPicUpLoad {
    private static Logger log = LoggerFactory.getLogger(MinioPicUpLoad.class);
    private static String ACCESS_KEY = "HSCBKASDKLKL";
    private static String SECRET_KET = "ASNXCJNASJnjksancascjnkjasc";
    private static String END_POINT = "http://162.245.236.170:9000";
    private static MinioClient minioclient;

    public MinioPicUpLoad() {
        try {
            minioclient = new MinioClient(END_POINT,ACCESS_KEY,SECRET_KET);
        } catch (InvalidEndpointException e) {
            e.printStackTrace();
        } catch (InvalidPortException e) {
            e.printStackTrace();
        }
    }

    public String push(String packageName, String fileName, String suffix, String fileUrl) {
        String path = "";
        try {
            boolean isExist = minioclient.bucketExists(packageName);
            if (isExist) {
                log.info(packageName + "已经存在了");
            } else {
                minioclient.makeBucket(packageName);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        try {
            minioclient.putObject(packageName, fileName + "." + suffix, fileUrl);
            path = END_POINT+"/"+packageName+"/"+fileName+"."+suffix;
            log.info(fileUrl + fileName + "." + suffix + "插入成功");

            return path;
        } catch (Exception e) {
            log.error(e.getMessage());
           return "";
        } finally {
            return path;
        }
//        log.info(fileUrl + fileName + "." + suffix + "插入成功");
//        return path;
    }
}
