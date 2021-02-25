package com.fl.model.clientReq;

import com.fl.model.AddTaskMinio;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AddSegmentQueue {

    private String filmName;

    private String btUrl;

//    private String subtitleUrl;

    private String filmSize;

    private String resolvingPower;

//    private String subtitleSuffix;

    private List<AddTaskMinio> minioInfo;

//    private String language;

    private String doubanId;

    private String filmRandom;

//    private String token;
//
//    private Integer userId;
}
