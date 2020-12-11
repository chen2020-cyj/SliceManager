package com.fl.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "bt_download")//指定表名
public class BtDownLoad {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String filmId;

    private String btState;

    private String startTime;

    private String updateTime;
}
