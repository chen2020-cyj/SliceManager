package com.fl.model.clientReq;

import lombok.Data;

@Data
public class FindAnnounce {
    private Integer offset;

    private Integer page;

    private String state;

    private String token;

    private Integer userId;
}
