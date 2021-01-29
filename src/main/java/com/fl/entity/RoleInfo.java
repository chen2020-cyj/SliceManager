package com.fl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class RoleInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer pid;

    private String name;

    private Integer deleteFlag;

    private String createTime;

    private String updateTime;
}
