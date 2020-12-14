package com.fl.model.sliceServerRes;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 请求 的任务接口 返回的数据
 */
@Data
public class ResSegmentManager {

    /**
     * 电影id
     */
    private String filmId;

    /**
     * 种子链接
     */
    private String btUrl;

    /**
     * 字幕文件地址
     */
    private String subtitleUrl;
    /**
     * 字幕文件后缀
     */
    private String subtitleSuffix;
    /**
     * 分辨率
     */
    private String resolvingPower;
    /**
     * 电影大小GB
     */
    private String filmSize;
    /**
     * doubanId
     */
    private String doubanId;
    /**
     * 桶消息体的  map
     * key ：分辨率名称
     * value： server的list消息体
     */
    private Map<String, String> msg;
}
