package com.fl.model.clientReq;

import lombok.Data;

@Data
public class FindAllFilmSource {

    private Integer offset;

    private Integer page;

    private String filmId;

    private String filmInfoId;

//    private String token;
//
//    private Integer userId;
}
