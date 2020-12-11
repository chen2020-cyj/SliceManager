package com.fl.model.clientReq;

import lombok.Data;

@Data
public class FindFilmInfo {
    private String token;

    private Integer userId;

    private String area;

    private Integer year;

    private String filmName;

    private Integer page;

    private Integer offset;

//    private String
}
