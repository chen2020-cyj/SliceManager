package com.fl.model.sliceServerRes;

import lombok.Data;

@Data
public class MinioServer {
    private String ip;
    private String accessKey;
    private String secretKey;
}
