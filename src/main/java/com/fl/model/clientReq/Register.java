package com.fl.model.clientReq;

import lombok.Data;

@Data
public class Register {
    private String username;

    private String name;

    private String password;

    private Integer groupId;

//    private String token;
//
//    private Integer userId;
}
