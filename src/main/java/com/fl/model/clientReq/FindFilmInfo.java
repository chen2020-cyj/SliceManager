package com.fl.model.clientReq;

import lombok.Data;

@Data
public class FindFilmInfo {

    private String area;

    private String year;

    private String tag;

    private Integer categoryId;

    private Integer page;

    private Integer offset;

//    private String
}
