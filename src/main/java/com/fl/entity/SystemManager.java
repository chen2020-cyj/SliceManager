package com.fl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "system_manager")
public class SystemManager {
    @TableId(type = IdType.AUTO)
    private Integer id;
    @TableField(value = "ready_minio_capacity")
    private double readyMinioCapacity;

    private String createTime;

    private String updateTime;
}
