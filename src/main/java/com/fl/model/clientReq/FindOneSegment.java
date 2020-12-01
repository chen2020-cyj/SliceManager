package com.fl.model.clientReq;

import lombok.Data;

@Data
public class FindOneSegment {
    private String filmId;

    private Integer userId;

    private String token;
}
