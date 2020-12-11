package com.fl.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "minio_manager")//指定表名
public class MinioInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * msg:json消息体，内容为地址，桶用户，桶密码
     */
    private Object msg;
    /**
     * 该桶总容量
     */
    private double totalCapacity;
    /**
     * 存储桶地区
     */
    private String area;
    /**
     * 桶的分辨率
     */
    private String resolvingPower;
    /**
     * 桶的类型
     */
    private String type;
    /**
     * 可用容量
     */

    private double availableCapacity;
    /**
     * 创建时间
     */
    private String createTime;

    /**
     *
     */
    private String updateTime;
}
