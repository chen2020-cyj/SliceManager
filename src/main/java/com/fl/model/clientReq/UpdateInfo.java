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

    private String filmChineseName;

    private String filmEnglishName;
    /**
     * 电影简介
     */
    private String introduction;

    private String imageUrl;

    private String tag;

//    private String film
}
