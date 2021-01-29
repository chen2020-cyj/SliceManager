package com.fl.model.clientRes;

import lombok.Data;

@Data
public class ResLog {
    private Integer userId;

    private String name;

    private String msg;

    private String createTime;
}
