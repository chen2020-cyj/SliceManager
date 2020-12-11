package com.fl.model.sliceServerReq;

import lombok.Data;

@Data
public class MinioBackMessage {
    /**
     * 原本的大小 filmSize
     */
    private String originalSize;

    /**
     * 实际使用的 大小
     */
    private String actualSize;

    /**
     * 成功的url地址
     */
    private String url;
}
