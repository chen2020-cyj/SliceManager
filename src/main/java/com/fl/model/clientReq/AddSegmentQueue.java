package com.fl.model.clientReq;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AddSegmentQueue {

    private String filmName;

    private String btUrl;

    private String subtitleUrl;

    private String filmSize;

    private String resolvingPower;

    private String subtitleSuffix;

    private Object minioInfo;

    private String language;

    private String doubanId;


//    private String token;
//
//    private Integer userId;
}
