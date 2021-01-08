package com.fl.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "user_info")//指定表名
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String username;

    private String password;

    private String token;

    private String name;

    private Integer groupId;

    private Integer roleId;

    private String tokenTime;

    private String createTime;

    private String updateTime;
}
