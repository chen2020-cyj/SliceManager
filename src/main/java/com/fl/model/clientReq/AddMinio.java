package com.fl.model.clientReq;

import lombok.Data;

import java.util.List;

@Data
public class AddMinio {
//    private String token;
//
//    private Integer userId;
    private String nickName;

    private String resolvingPower;

    private double totalCapacity;

    private double availableCapacity;

    private String type;

    private String area;

    private List<AddServerInfo> data;
}
