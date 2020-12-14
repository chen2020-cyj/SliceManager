package com.fl.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class FileUtils {
    /**
     * @param file     文件
     * @param path     文件存放路径
     * @param fileName 保存的文件名
     * @return
     */
    private static String path = "D:\\uploadImage";
    public static String upload(MultipartFile file) {

        //确定上传的文件名
        String realPath = path + "\\" + getRandomString(10)+".jpg";
        System.out.println("上传文件：" + realPath);

        File dest = new File(realPath);

        //判断文件父目录是否存在
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdir();
        }

        try {
            //保存文件
            file.transferTo(dest);
            return realPath;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

    }

    public static String getRandomString(int length){
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<length;i++){
            int number=random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 修改文件名
     * @param filePath
     * @param newFileName
     * @return
     */
    public static String fixFileName(String filePath, String newFileName) {
        File f = new File(filePath);
        if (!f.exists()) { // 判断原文件是否存在（防止文件名冲突）
            return null;
        }
        newFileName = newFileName.trim();
        if ("".equals(newFileName) || newFileName == null) // 文件名不能为空
            return null;
        String newFilePath = null;
        if (f.isDirectory()) { // 判断是否为文件夹
            newFilePath = filePath.substring(0, filePath.lastIndexOf("\\")) + "\\" + newFileName;
        } else {
            newFilePath = filePath.substring(0, filePath.lastIndexOf("\\")) + "\\" + newFileName
                    + filePath.substring(filePath.lastIndexOf("."));
        }
        File nf = new File(newFilePath);
        try {
            f.renameTo(nf); // 修改文件名
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
        return newFilePath;
    }

}
