package com.fl.model.clientReq;


import lombok.Data;

@Data
public class AdminFindFilm {
    private String filmId;

    private String token;

    private Integer userId;
}
