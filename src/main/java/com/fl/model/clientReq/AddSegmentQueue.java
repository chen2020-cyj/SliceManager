package com.fl.model.clientReq;

import lombok.Data;

@Data
public class AddSegmentQueue {

    private String filmName;

    private String btUrl;

    private String subtitleUrl;

    private double filmSize;

    private String resolvingPower;

    private String subtitleSuffix;

    private String token;

    private Integer userId;
}
