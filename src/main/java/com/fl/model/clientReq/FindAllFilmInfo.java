package com.fl.model.clientReq;

import lombok.Data;

@Data
public class FindAllFilmInfo {

    private Integer page;

    private Integer offset;

    private String area;

    private String year;

    private String tag;

    private String categoryId;

    private String filmId;

    private String whetherUpload;
}
