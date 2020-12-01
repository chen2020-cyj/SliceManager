package com.fl.model.clientReq;

import lombok.Data;

@Data
public class AddServerInfo {
    private String accessKey;

    private String secretKey;

    private String ip;
}

