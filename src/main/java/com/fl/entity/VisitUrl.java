package com.fl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "visit_url")//指定表名
public class VisitUrl {
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * cdn路径  json 消息体
     */
    private String cdnUrl;

    /**
     * ngixn路径  json消息体
     */
    private String nginxUrl;

    /**
     * 存储桶路径  json消息体
     */
    private String minioUrl;

    /**
     * 对应片源filmId
     */
    private String doubanId;

    private String createTime;

    private String updateTime;
}
