package com.fl.model.clientReq;

import lombok.Data;

@Data
public class SelectFilmReq {
    private String filmId;

    private Integer resolvingPower;

    private String token;
}
