package com.fl.model.clientReq;

import lombok.Data;

@Data
public class UpdateToken {

    private Integer userId;

    private String username;

    private String token;
}
