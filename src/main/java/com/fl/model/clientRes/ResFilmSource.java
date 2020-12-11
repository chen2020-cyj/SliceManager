package com.fl.model.clientRes;

import lombok.Data;

@Data
public class ResFilmSource {
    private Integer id;

    private String filmId;

    private Integer filmInfoId;

    private String filmName;

    private Object minioUrl;

    private String BtUrl;

    private String subtitleUrl;

    private String language;

    private String resolvingPower;

    private String createTime;

    private String updateTime;
}
