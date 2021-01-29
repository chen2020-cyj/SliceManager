package com.fl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "operation_log")
public class OperationLog {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Object userId;

    private String msg;

    private String createTime;

    private String updateTime;

}
