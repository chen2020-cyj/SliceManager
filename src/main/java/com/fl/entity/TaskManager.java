package com.fl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "task_manager")
public class TaskManager {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 电影id
     */
    private String filmId;
    /**
     * 对应存储桶的id
     */
    private String minioId;
    /**
     * 对应电影名称
     */
    private String filmName;
    /**
     * 下载状态
     */
    private String downloadState;
    /**
     * 上传状态
     */
    private Object uploadState;
    /**
     * 链接是否有效
     */
    private String linkState;
    /**
     * 种子链接
     */
    private String btUrl;
    /**
     * 切片状态
     */
    private Object segmentState;
    /**
     * 字幕文件地址
     */
    private String subtitleUrl;
    /**
     * 字幕文件后缀
     */
    private String subtitleSuffix;
    /**
     * 电影大小
     */
    private String filmSize;
    /**
     * 放入到存储桶路径
     */
    private String minioUrl;
    /**
     * 对应语言表id
     */
    private String languageId;
    /**
     * 是否需要爬虫
     */
    private String doubanId;
    /**
     * 分辨率
     */
    private String resolvingPower;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 更新时间
     */
    private String updateTime;
}
