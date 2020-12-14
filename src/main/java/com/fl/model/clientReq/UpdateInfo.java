package com.fl.model.clientReq;

import com.fl.entity.FilmInfo;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class UpdateInfo {
    /**
     * 电影信息id
     */
    private Integer filmInfoId;

    private String chineseName;

    private String englishName;
    /**
     * 电影简介
     */
    private String description;

    private String filmCoverImage;

    private String tag;

    private MultipartFile file;
//    private String film
}
