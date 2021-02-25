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
     * 字幕链接路径  json消息体
     */
    private String subtitleUrl;

    /**
     * 存储桶路径  json消息体
     */
    private String minioUrl;

    /**
     * 对应对应影片信息随机生成Id
     */
    private String filmRandom;

    private String createTime;

    private String updateTime;
}
