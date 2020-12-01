package com.fl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "filmsource_manager")//指定表名
public class FilmSourceRecord {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 电影id
     */
    private String filmId;
    /**
     * minio 对应存储桶id
     */
    private String minioUrl;
    /**
     *对应电影信息表
     */
    private Integer film_info_id;
    /**
     * 对应电影名称
     */
    private String filmName;
    /**
     * bt种子路径
     */
    private String btUrl;
    /**
     * 字幕文件路径
     */
    private String subtitleUrl;
    /**
     * 分辨率
     */
    private String resolvingPower;
    /**
     * 创建时间
     */
    private String createTime;
}
