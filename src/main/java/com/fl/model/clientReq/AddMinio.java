package com.fl.model.clientReq;

import lombok.Data;

import java.util.List;

@Data
public class AddMinio {
    private String token;

    private Integer userId;

    private String resolvingPower;

    private double totalCapacity;

    private String area;

    private List<AddServerInfo> data;
}
