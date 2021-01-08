package com.fl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "nginx_manager")//指定表名
public class NginxManager {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String nginxUrl;

    private Integer minioId;

    private String createTime;

    private String updateTime;
    
}
