package com.fl.entity;

import lombok.Data;

@Data
public class NginxManager {
    private Integer id;

    private String nginxUrl;

    private Integer minioId;

    private String createTime;

    private String updateTime;
    
}
