package com.fl.model.clientReq;

import lombok.Data;

@Data
public class FindAllMinio {
    private String token;

    private Integer userId;

    private Integer offset;

    private Integer page;

    private String resolvingPower;
}
