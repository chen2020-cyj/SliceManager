package com.fl.model.clientReq;

import lombok.Data;

@Data
public class FindSegmentTask {

    private Integer offset;

    private Integer page;

    private String filmId;

    private String segmentState;

    private String downloadState;
    /**
     * 链接是否有效 1001 种子无效 1002 字幕无效  1003种子链接有效但是没有资源
     */
    private String linkState;

//    private String token;
//
//    private String userId;

}
