package com.fl.model.clientReq;

import lombok.Data;

@Data
public class FindSegmentTask {

    private Integer offset;

    private Integer page;

    private String token;

    private Integer userId;

}
