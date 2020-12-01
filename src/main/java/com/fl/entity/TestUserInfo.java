package com.fl.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "user")//指定表名
public class TestUserInfo {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String introduction;

    private String avatar;

    private String name;

    private String roles;

}
