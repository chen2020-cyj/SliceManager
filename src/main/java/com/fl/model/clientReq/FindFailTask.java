package com.fl.model.clientReq;

import lombok.Data;

@Data
public class FindFailTask {
    private Integer offset;

    private Integer page;

    private String token;

    private Integer userId;
}
